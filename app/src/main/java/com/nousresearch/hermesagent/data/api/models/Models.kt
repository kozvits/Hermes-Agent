package com.nousresearch.hermesagent.data.api.models

import com.google.gson.annotations.SerializedName

// ── Chat Messages ──
data class ChatMessage(
    @SerializedName("role") val role: String,      // "user", "assistant", "system", "tool"
    @SerializedName("content") val content: Any?,   // String or List<ContentPart>
    @SerializedName("tool_calls") val toolCalls: List<ToolCall>? = null,
    @SerializedName("tool_call_id") val toolCallId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("timestamp") val timestamp: Long? = null,
)

data class ContentPart(
    @SerializedName("type") val type: String,       // "text", "image_url", "tool_use", "tool_result"
    @SerializedName("text") val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrl? = null,
    @SerializedName("source") val source: ImageSource? = null,
)

data class ImageUrl(
    @SerializedName("url") val url: String,
    @SerializedName("detail") val detail: String? = null,
)

data class ImageSource(
    @SerializedName("type") val type: String = "base64",
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("data") val data: String,
)

data class ToolCall(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String = "function",
    @SerializedName("function") val function: ToolFunction,
)

data class ToolFunction(
    @SerializedName("name") val name: String,
    @SerializedName("arguments") val arguments: String,
)

// ── Chat Request / Response ──
data class ChatRequest(
    @SerializedName("model") val model: String = "default",
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("stream") val stream: Boolean = true,
    @SerializedName("temperature") val temperature: Double? = null,
    @SerializedName("max_tokens") val maxTokens: Int? = null,
    @SerializedName("tools") val tools: List<ToolSpec>? = null,
    @SerializedName("system") val system: String? = null,
)

data class ChatResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("object") val `object`: String? = null,
    @SerializedName("created") val created: Long? = null,
    @SerializedName("model") val model: String? = null,
    @SerializedName("choices") val choices: List<Choice> = emptyList(),
    @SerializedName("usage") val usage: Usage? = null,
    @SerializedName("error") val error: ApiError? = null,
)

data class Choice(
    @SerializedName("index") val index: Int = 0,
    @SerializedName("delta") val delta: Delta? = null,
    @SerializedName("message") val message: ChatMessage? = null,
    @SerializedName("finish_reason") val finishReason: String? = null,
)

data class Delta(
    @SerializedName("role") val role: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("tool_calls") val toolCalls: List<ToolCall>? = null,
)

data class Usage(
    @SerializedName("prompt_tokens") val promptTokens: Int = 0,
    @SerializedName("completion_tokens") val completionTokens: Int = 0,
    @SerializedName("total_tokens") val totalTokens: Int = 0,
)

data class ApiError(
    @SerializedName("message") val message: String = "",
    @SerializedName("type") val type: String? = null,
    @SerializedName("code") val code: String? = null,
)

// ── Tool Specification ──
data class ToolSpec(
    @SerializedName("type") val type: String = "function",
    @SerializedName("function") val function: ToolSpecFunction,
)

data class ToolSpecFunction(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("parameters") val parameters: Map<String, Any?> = emptyMap(),
)

// ── Provider ──
data class Provider(
    @SerializedName("name") val name: String,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("provider") val provider: String? = null,
    @SerializedName("model") val model: String? = null,
    @SerializedName("api_key") val apiKey: String? = null,
    @SerializedName("base_url") val baseUrl: String? = null,
    @SerializedName("is_active") val isActive: Boolean = false,
    @SerializedName("status") val status: String = "unknown",  // "connected", "disconnected", "error"
)

// ── Skill ──
data class Skill(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("version") val version: String = "1.0.0",
    @SerializedName("category") val category: String? = null,
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("author") val author: String? = null,
    @SerializedName("installed") val installed: Boolean = false,
    @SerializedName("enabled") val enabled: Boolean = true,
    @SerializedName("updated_at") val updatedAt: String? = null,
)

// ── Session ──
data class Session(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String = "Новая сессия",
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("message_count") val messageCount: Int = 0,
    @SerializedName("model") val model: String? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("preview") val preview: String? = null,
    @SerializedName("is_active") val isActive: Boolean = false,
)

// ── Cron Job ──
data class CronJob(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("schedule") val schedule: String,
    @SerializedName("prompt") val prompt: String = "",
    @SerializedName("enabled") val enabled: Boolean = true,
    @SerializedName("last_run") val lastRun: String? = null,
    @SerializedName("next_run") val nextRun: String? = null,
    @SerializedName("run_count") val runCount: Int = 0,
    @SerializedName("last_status") val lastStatus: String? = null,
)

// ── Tool (toolset) ──
data class ToolInfo(
    @SerializedName("name") val name: String,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("description") val description: String = "",
    @SerializedName("toolset") val toolset: String = "",
    @SerializedName("enabled") val enabled: Boolean = true,
    @SerializedName("requires_env") val requiresEnv: List<String> = emptyList(),
)

// ── Memory Entry ──
data class MemoryEntry(
    @SerializedName("id") val id: String? = null,
    @SerializedName("content") val content: String,
    @SerializedName("target") val target: String = "memory",  // "memory" or "user"
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
)

// ── Connection Status ──
data class ConnectionStatus(
    @SerializedName("status") val status: String = "disconnected", // "connecting", "connected", "disconnected", "error"
    @SerializedName("server_url") val serverUrl: String = "",
    @SerializedName("model") val model: String = "",
    @SerializedName("provider") val provider: String = "",
    @SerializedName("version") val version: String = "",
    @SerializedName("error_message") val errorMessage: String? = null,
)

// ── Tool Execution Result (SSE streaming) ──
data class ToolExecutionEvent(
    @SerializedName("type") val type: String,  // "tool_start", "tool_output", "tool_end", "text_delta", "error"
    @SerializedName("tool_name") val toolName: String? = null,
    @SerializedName("tool_id") val toolId: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("args") val args: Map<String, Any?>? = null,
)
