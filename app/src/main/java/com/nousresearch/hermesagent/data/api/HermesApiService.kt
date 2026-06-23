package com.nousresearch.hermesagent.data.api

import com.nousresearch.hermesagent.data.api.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface HermesApiService {

    // ── Chat ──
    @POST("v1/chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): Response<ChatResponse>

    @POST("v1/chat/completions")
    @Streaming
    suspend fun chatCompletionStream(@Body request: ChatRequest): Response<ResponseBody>

    // ── Status ──
    @GET("api/status")
    suspend fun getStatus(): Response<ConnectionStatus>

    // ── Models ──
    @GET("v1/models")
    suspend fun listModels(): Response<ModelsResponse>

    // ── Providers ──
    @GET("api/providers")
    suspend fun listProviders(): Response<List<Provider>>

    @POST("api/providers/activate")
    suspend fun activateProvider(@Body request: Map<String, String>): Response<Provider>

    // ── Sessions ──
    @GET("api/sessions")
    suspend fun listSessions(): Response<List<Session>>

    @GET("api/sessions/{id}")
    suspend fun getSession(@Path("id") id: String): Response<Session>

    @GET("api/sessions/{id}/messages")
    suspend fun getSessionMessages(@Path("id") id: String): Response<List<ChatMessage>>

    @DELETE("api/sessions/{id}")
    suspend fun deleteSession(@Path("id") id: String): Response<Unit>

    @POST("api/sessions/{id}/rename")
    suspend fun renameSession(
        @Path("id") id: String,
        @Body request: Map<String, String>,
    ): Response<Session>

    // ── Skills ──
    @GET("api/skills")
    suspend fun listSkills(): Response<List<Skill>>

    @POST("api/skills/{name}/toggle")
    suspend fun toggleSkill(@Path("name") name: String): Response<Skill>

    @POST("api/skills/{name}/install")
    suspend fun installSkill(@Path("name") name: String): Response<Skill>

    @DELETE("api/skills/{name}")
    suspend fun uninstallSkill(@Path("name") name: String): Response<Unit>

    // ── Cron ──
    @GET("api/cron")
    suspend fun listCronJobs(): Response<List<CronJob>>

    @POST("api/cron")
    suspend fun createCronJob(@Body job: CronJob): Response<CronJob>

    @PUT("api/cron/{id}")
    suspend fun updateCronJob(
        @Path("id") id: String,
        @Body job: CronJob,
    ): Response<CronJob>

    @POST("api/cron/{id}/toggle")
    suspend fun toggleCronJob(@Path("id") id: String): Response<CronJob>

    @DELETE("api/cron/{id}")
    suspend fun deleteCronJob(@Path("id") id: String): Response<Unit>

    // ── Tools ──
    @GET("api/tools")
    suspend fun listTools(): Response<List<ToolInfo>>

    @POST("api/tools/{name}/toggle")
    suspend fun toggleTool(@Path("name") name: String): Response<ToolInfo>

    // ── Memory ──
    @GET("api/memory")
    suspend fun listMemoryEntries(): Response<List<MemoryEntry>>

    @POST("api/memory")
    suspend fun addMemoryEntry(@Body entry: MemoryEntry): Response<MemoryEntry>

    @DELETE("api/memory/{id}")
    suspend fun deleteMemoryEntry(@Path("id") id: String): Response<Unit>

    // ── Config ──
    @GET("api/config")
    suspend fun getConfig(): Response<Map<String, Any>>

    @PUT("api/config")
    suspend fun updateConfig(@Body config: Map<String, Any>): Response<Map<String, Any>>
}

data class ModelsResponse(
    val data: List<ModelInfo> = emptyList(),
)

data class ModelInfo(
    val id: String,
    val `object`: String = "model",
    val created: Long? = null,
    val owned_by: String? = null,
)
