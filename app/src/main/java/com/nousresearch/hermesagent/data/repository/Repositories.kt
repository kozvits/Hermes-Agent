package com.nousresearch.hermesagent.data.repository

import com.google.gson.Gson
import com.nousresearch.hermesagent.data.api.HermesApiService
import com.nousresearch.hermesagent.data.api.HermesStreamingClient
import com.nousresearch.hermesagent.data.api.models.*
import com.nousresearch.hermesagent.data.local.HermesDatabase
import com.nousresearch.hermesagent.data.local.MessageEntity
import com.nousresearch.hermesagent.data.local.SessionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HermesRepository @Inject constructor(
    private val api: HermesApiService,
    private val database: HermesDatabase,
    private val gson: Gson,
) {
    private val dao = database.messageDao()
    private val sessionDao = database.sessionDao()

    // ── Session Management ──
    fun getAllSessions(): Flow<List<SessionEntity>> = sessionDao.getAllSessions()

    suspend fun getLatestSession(): SessionEntity? = sessionDao.getLatestSession()

    suspend fun createSession(model: String? = null): SessionEntity {
        val session = SessionEntity(
            id = UUID.randomUUID().toString().take(12),
            model = model,
            isActive = true,
        )
        sessionDao.deactivateAllSessions()
        sessionDao.insertSession(session)
        return session
    }

    suspend fun switchSession(sessionId: String) {
        sessionDao.deactivateAllSessions()
        sessionDao.activateSession(sessionId)
    }

    suspend fun deleteSession(sessionId: String) {
        dao.deleteSessionMessages(sessionId)
        sessionDao.deleteSession(SessionEntity(id = sessionId))
    }

    suspend fun renameSession(sessionId: String, title: String) {
        sessionDao.renameSession(sessionId, title)
    }

    // ── Messages ──
    fun getMessages(sessionId: String): Flow<List<MessageEntity>> = dao.getMessages(sessionId)

    suspend fun saveUserMessage(
        sessionId: String,
        content: String,
    ): Long {
        val msg = MessageEntity(
            sessionId = sessionId,
            role = "user",
            content = content,
        )
        val id = dao.insertMessage(msg)
        sessionDao.incrementMessageCount(sessionId)
        sessionDao.updateTimestamp(sessionId, System.currentTimeMillis())
        return id
    }

    suspend fun saveAssistantMessage(
        sessionId: String,
        content: String,
    ): Long {
        val msg = MessageEntity(
            sessionId = sessionId,
            role = "assistant",
            content = content,
        )
        val id = dao.insertMessage(msg)
        sessionDao.incrementMessageCount(sessionId)
        sessionDao.updateTimestamp(sessionId, System.currentTimeMillis())
        return id
    }

    suspend fun saveToolMessage(
        sessionId: String,
        toolName: String,
        content: String,
    ): Long {
        val msg = MessageEntity(
            sessionId = sessionId,
            role = "tool",
            content = content,
            toolName = toolName,
        )
        return dao.insertMessage(msg)
    }

    suspend fun clearSessionMessages(sessionId: String) {
        dao.deleteSessionMessages(sessionId)
    }

    // ── API Calls ──
    // (the streaming client is used directly in the ViewModel)
}

@Singleton
class ServerRepository @Inject constructor(
    private val api: HermesApiService,
) {
    suspend fun getStatus(): Result<ConnectionStatus> = runCatching {
        val response = api.getStatus()
        if (response.isSuccessful) response.body()!!
        else throw Exception("HTTP ${response.code()}: ${response.message()}")
    }

    suspend fun listModels(): Result<List<ModelInfo>> = runCatching {
        val response = api.listModels()
        if (response.isSuccessful) response.body()?.data ?: emptyList()
        else throw Exception("HTTP ${response.code()}: ${response.message()}")
    }

    suspend fun listProviders(): Result<List<Provider>> = runCatching {
        val response = api.listProviders()
        if (response.isSuccessful) response.body() ?: emptyList()
        else throw Exception("HTTP ${response.code()}: ${response.message()}")
    }

    suspend fun activateProvider(name: String): Result<Provider> = runCatching {
        val response = api.activateProvider(mapOf("name" to name))
        if (response.isSuccessful) response.body()!!
        else throw Exception("HTTP ${response.code()}: ${response.message()}")
    }

    suspend fun listSkills(): Result<List<Skill>> = runCatching {
        val response = api.listSkills()
        if (response.isSuccessful) response.body() ?: emptyList()
        else throw Exception("HTTP ${response.code()}: ${response.message()}")
    }

    suspend fun listSessions(): Result<List<Session>> = runCatching {
        val response = api.listSessions()
        if (response.isSuccessful) response.body() ?: emptyList()
        else throw Exception("HTTP ${response.code()}: ${response.message()}")
    }

    suspend fun listCronJobs(): Result<List<CronJob>> = runCatching {
        val response = api.listCronJobs()
        if (response.isSuccessful) response.body() ?: emptyList()
        else throw Exception("HTTP ${response.code()}: ${response.message()}")
    }

    suspend fun listTools(): Result<List<ToolInfo>> = runCatching {
        val response = api.listTools()
        if (response.isSuccessful) response.body() ?: emptyList()
        else throw Exception("HTTP ${response.code()}: ${response.message()}")
    }

    suspend fun listMemoryEntries(): Result<List<MemoryEntry>> = runCatching {
        val response = api.listMemoryEntries()
        if (response.isSuccessful) response.body() ?: emptyList()
        else throw Exception("HTTP ${response.code()}: ${response.message()}")
    }
}

data class ServerConfig(
    val serverUrl: String,
    val apiKey: String?,
)
