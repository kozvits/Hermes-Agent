package com.nousresearch.hermesagent.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nousresearch.hermesagent.data.api.HermesApiService
import com.nousresearch.hermesagent.data.api.HermesStreamingClient
import com.nousresearch.hermesagent.data.api.models.*
import com.nousresearch.hermesagent.data.local.MessageEntity
import com.nousresearch.hermesagent.data.local.PreferencesManager
import com.nousresearch.hermesagent.data.repository.HermesRepository
import com.nousresearch.hermesagent.data.repository.ServerConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatState(
    val messages: List<MessageEntity> = emptyList(),
    val currentSessionId: String = "",
    val isStreaming: Boolean = false,
    val isConnecting: Boolean = false,
    val error: String? = null,
    val connectionStatus: String = "disconnected", // "connected", "disconnected", "connecting", "error"
    val serverUrl: String = "http://10.0.2.2:8080",
    val currentModel: String = "default",
    val currentProvider: String = "",
    val statusMessage: String = "",
    val serverVersion: String = "",
)

data class StreamState(
    val isActive: Boolean = false,
    val accumulatedContent: String = "",
    val activeTools: List<ActiveTool> = emptyList(),
    val isThinking: Boolean = false,
)

data class ActiveTool(
    val id: String?,
    val name: String,
    val status: String = "running", // "running", "completed", "error"
    val output: String = "",
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application,
    private val repository: HermesRepository,
    private val prefs: PreferencesManager,
    private val gson: Gson,
) : AndroidViewModel(application) {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    private val _streamState = MutableStateFlow(StreamState())
    val streamState: StateFlow<StreamState> = _streamState.asStateFlow()

    private var streamingClient: HermesStreamingClient? = null
    private var currentSessionId: String = ""
    private val apiKey: String = ""

    init {
        // Load preferences
        viewModelScope.launch {
            prefs.serverUrl.collect { url ->
                _chatState.update { it.copy(serverUrl = url) }
                updateStreamingClient(url)
            }
        }
        viewModelScope.launch {
            prefs.model.collect { model ->
                _chatState.update { it.copy(currentModel = model) }
            }
        }
        viewModelScope.launch {
            prefs.provider.collect { provider ->
                _chatState.update { it.copy(currentProvider = provider) }
            }
        }
        // Initialize session
        viewModelScope.launch {
            val existing = repository.getLatestSession()
            if (existing != null) {
                currentSessionId = existing.id
                _chatState.update {
                    it.copy(
                        currentSessionId = existing.id,
                        currentModel = existing.model ?: it.currentModel,
                    )
                }
                repository.getMessages(existing.id).collect { msgs ->
                    _chatState.update { it.copy(messages = msgs) }
                }
            } else {
                createNewSession()
            }
        }
    }

    private fun updateStreamingClient(url: String) {
        streamingClient = HermesStreamingClient(
            baseUrl = url,
            apiKey = apiKey.ifBlank { null },
        )
    }

    fun createNewSession() {
        viewModelScope.launch {
            val session = repository.createSession(
                model = chatState.value.currentModel.ifBlank { null },
            )
            currentSessionId = session.id
            _chatState.update {
                it.copy(
                    currentSessionId = session.id,
                    messages = emptyList(),
                    error = null,
                )
            }
            repository.getMessages(session.id).collect { msgs ->
                _chatState.update { it.copy(messages = msgs) }
            }
        }
    }

    fun switchSession(sessionId: String) {
        viewModelScope.launch {
            repository.switchSession(sessionId)
            currentSessionId = sessionId
            _chatState.update { it.copy(currentSessionId = sessionId) }
            repository.getMessages(sessionId).collect { msgs ->
                _chatState.update { it.copy(messages = msgs) }
            }
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            if (sessionId == currentSessionId) {
                createNewSession()
            }
        }
    }

    fun renameSession(sessionId: String, title: String) {
        viewModelScope.launch {
            repository.renameSession(sessionId, title)
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearSessionMessages(currentSessionId)
            _chatState.update { it.copy(messages = emptyList()) }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _streamState.value.isActive) return

        viewModelScope.launch {
            // Save user message
            repository.saveUserMessage(currentSessionId, text)

            // Build messages payload
            val dbMessages = repository.getMessages(currentSessionId).first()
            val apiMessages = dbMessages.map { msg ->
                ChatMessage(
                    role = msg.role,
                    content = msg.content,
                    toolCallId = if (msg.role == "tool") msg.toolName else null,
                    name = msg.toolName,
                )
            }

            // Start streaming
            val client = streamingClient ?: return@launch
            _streamState.update { StreamState(isActive = true, isThinking = true) }
            _chatState.update { it.copy(isStreaming = true, error = null) }

            val request = ChatRequest(
                model = chatState.value.currentModel,
                messages = apiMessages,
                stream = true,
            )

            var assistantContent = ""

            client.chatCompletionStream(request).collect { event ->
                when (event.type) {
                    "text_delta" -> {
                        assistantContent += event.content ?: ""
                        _streamState.update {
                            it.copy(
                                isThinking = false,
                                accumulatedContent = assistantContent,
                            )
                        }
                    }
                    "tool_start" -> {
                        val tool = ActiveTool(
                            id = event.toolId,
                            name = event.toolName ?: "unknown",
                        )
                        _streamState.update {
                            it.copy(
                                isThinking = false,
                                activeTools = it.activeTools + tool,
                            )
                        }
                    }
                    "tool_output" -> {
                        val output = event.content ?: ""
                        _streamState.update { state ->
                            val updated = state.activeTools.map { tool ->
                                if (tool.id == event.toolId) {
                                    tool.copy(output = tool.output + output)
                                } else tool
                            }
                            state.copy(activeTools = updated)
                        }
                    }
                    "stream_end" -> {
                        // Save assistant message
                        if (assistantContent.isNotBlank()) {
                            repository.saveAssistantMessage(
                                currentSessionId,
                                assistantContent,
                            )
                        }
                        _streamState.update { StreamState() }
                        _chatState.update { it.copy(isStreaming = false) }
                    }
                    "error" -> {
                        _streamState.update { StreamState() }
                        _chatState.update {
                            it.copy(
                                isStreaming = false,
                                error = event.content ?: "Unknown error",
                            )
                        }
                    }
                    "stream_complete", "stream_closed" -> {
                        if (assistantContent.isNotBlank()) {
                            repository.saveAssistantMessage(
                                currentSessionId,
                                assistantContent,
                            )
                        }
                        _streamState.update { StreamState() }
                        _chatState.update { it.copy(isStreaming = false) }
                    }
                }
            }
        }
    }

    fun connectToServer(url: String? = null) {
        viewModelScope.launch {
            val serverUrl = url ?: chatState.value.serverUrl
            _chatState.update { it.copy(isConnecting = true, connectionStatus = "connecting", error = null) }

            prefs.setServerUrl(serverUrl)
            updateStreamingClient(serverUrl)

            try {
                // Simple health check
                val client = HermesStreamingClient(serverUrl, apiKey.ifBlank { null })
                // We just update the status — the actual connection is verified on first message
                _chatState.update {
                    it.copy(
                        isConnecting = false,
                        connectionStatus = "connected",
                        statusMessage = "Подключено к $serverUrl",
                    )
                }
            } catch (e: Exception) {
                _chatState.update {
                    it.copy(
                        isConnecting = false,
                        connectionStatus = "error",
                        error = "Ошибка подключения: ${e.message}",
                    )
                }
            }
        }
    }

    fun disconnectServer() {
        streamingClient = null
        _chatState.update {
            it.copy(
                connectionStatus = "disconnected",
                statusMessage = "Отключено",
            )
        }
    }

    fun clearError() {
        _chatState.update { it.copy(error = null) }
    }
}
