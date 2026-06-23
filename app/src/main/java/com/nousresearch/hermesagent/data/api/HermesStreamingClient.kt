package com.nousresearch.hermesagent.data.api

import com.google.gson.Gson
import com.nousresearch.hermesagent.data.api.models.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * SSE-based streaming client for Hermes Agent chat completions.
 * Receives tool calls, text deltas, and status events in real time.
 */
class HermesStreamingClient(
    private val baseUrl: String,
    private val apiKey: String? = null,
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json".toMediaType()

    /**
     * Flow that emits parsed [ToolExecutionEvent] from the SSE stream.
     */
    fun chatCompletionStream(request: ChatRequest): Flow<ToolExecutionEvent> = callbackFlow {
        val jsonBody = gson.toJson(request.copy(stream = true))
        val body = jsonBody.toRequestBody(jsonMediaType)

        val httpRequest = Request.Builder()
            .url("$baseUrl/v1/chat/completions")
            .post(body)
            .apply {
                if (!apiKey.isNullOrBlank()) {
                    addHeader("Authorization", "Bearer $apiKey")
                }
            }
            .addHeader("Accept", "text/event-stream")
            .build()

        var currentToolId: String? = null
        var currentToolName: String? = null

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String,
            ) {
                if (data == "[DONE]") {
                    trySend(ToolExecutionEvent(type = "stream_complete"))
                    return
                }

                try {
                    val chatResponse = gson.fromJson(data, ChatResponse::class.java)

                    // Check for API errors
                    if (chatResponse.error != null) {
                        trySend(
                            ToolExecutionEvent(
                                type = "error",
                                content = chatResponse.error.message,
                            )
                        )
                        return
                    }

                    for (choice in chatResponse.choices) {
                        val delta = choice.delta ?: continue

                        // Tool calls
                        if (!delta.toolCalls.isNullOrEmpty()) {
                            for (tc in delta.toolCalls) {
                                if (tc.id != null) {
                                    currentToolId = tc.id
                                    currentToolName = tc.function.name
                                    trySend(
                                        ToolExecutionEvent(
                                            type = "tool_start",
                                            toolName = tc.function.name,
                                            toolId = tc.id,
                                            args = parseJsonArgs(tc.function.arguments),
                                        )
                                    )
                                } else {
                                    // Accumulated arguments
                                    trySend(
                                        ToolExecutionEvent(
                                            type = "tool_output",
                                            toolName = currentToolName,
                                            toolId = currentToolId,
                                            content = tc.function.arguments,
                                        )
                                    )
                                }
                            }
                        }

                        // Text delta
                        if (!delta.content.isNullOrBlank()) {
                            trySend(
                                ToolExecutionEvent(
                                    type = "text_delta",
                                    content = delta.content,
                                )
                            )
                        }

                        // Finish
                        if (choice.finishReason != null) {
                            trySend(
                                ToolExecutionEvent(
                                    type = "stream_end",
                                    content = choice.finishReason,
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    trySend(
                        ToolExecutionEvent(
                            type = "error",
                            content = "Parse error: ${e.message}",
                        )
                    )
                }
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?,
            ) {
                val errorMsg = when {
                    t != null -> "Connection error: ${t.message}"
                    response != null -> "HTTP ${response.code}: ${response.message}"
                    else -> "Unknown error"
                }
                trySend(ToolExecutionEvent(type = "error", content = errorMsg))
                close(t)
            }

            override fun onClosed(eventSource: EventSource) {
                trySend(ToolExecutionEvent(type = "stream_closed"))
                close()
            }
        }

        val eventSource = EventSources.createFactory(client)
            .newEventSource(httpRequest, listener)

        awaitClose {
            eventSource.cancel()
        }
    }

    private fun parseJsonArgs(args: String?): Map<String, Any?>? {
        if (args.isNullOrBlank()) return null
        return try {
            gson.fromJson(args, Map::class.java) as? Map<String, Any?>
        } catch (e: Exception) {
            null
        }
    }
}
