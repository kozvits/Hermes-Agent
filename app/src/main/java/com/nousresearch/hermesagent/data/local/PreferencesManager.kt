package com.nousresearch.hermesagent.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hermes_settings")

class PreferencesManager(private val context: Context) {

    // ── Keys ──
    private object Keys {
        val SERVER_URL = stringPreferencesKey("server_url")
        val API_KEY = stringPreferencesKey("api_key")
        val THEME_MODE = stringPreferencesKey("theme_mode")        // "system", "light", "dark"
        val MODEL = stringPreferencesKey("model")
        val PROVIDER = stringPreferencesKey("provider")
        val STREAMING_ENABLED = booleanPreferencesKey("streaming_enabled")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val COMPACT_MODE = booleanPreferencesKey("compact_mode")
        val VOICE_INPUT_ENABLED = booleanPreferencesKey("voice_input_enabled")
    }

    // ── Flows ──
    val serverUrl: Flow<String> = context.dataStore.data.map { it[Keys.SERVER_URL] ?: "http://10.0.2.2:8080" }
    val apiKey: Flow<String> = context.dataStore.data.map { it[Keys.API_KEY] ?: "" }
    val themeMode: Flow<String> = context.dataStore.data.map { it[Keys.THEME_MODE] ?: "system" }
    val model: Flow<String> = context.dataStore.data.map { it[Keys.MODEL] ?: "default" }
    val provider: Flow<String> = context.dataStore.data.map { it[Keys.PROVIDER] ?: "" }
    val streamingEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.STREAMING_ENABLED] ?: true }
    val dynamicColors: Flow<Boolean> = context.dataStore.data.map { it[Keys.DYNAMIC_COLORS] ?: true }
    val firstLaunch: Flow<Boolean> = context.dataStore.data.map { it[Keys.FIRST_LAUNCH] ?: true }
    val compactMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.COMPACT_MODE] ?: false }
    val voiceInputEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.VOICE_INPUT_ENABLED] ?: false }

    // ── Suspend setters ──
    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { it[Keys.SERVER_URL] = url }
    }

    suspend fun setApiKey(key: String) {
        context.dataStore.edit { it[Keys.API_KEY] = key }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setModel(model: String) {
        context.dataStore.edit { it[Keys.MODEL] = model }
    }

    suspend fun setProvider(provider: String) {
        context.dataStore.edit { it[Keys.PROVIDER] = provider }
    }

    suspend fun setStreamingEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.STREAMING_ENABLED] = enabled }
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLORS] = enabled }
    }

    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { it[Keys.FIRST_LAUNCH] = false }
    }

    suspend fun setCompactMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.COMPACT_MODE] = enabled }
    }

    suspend fun setVoiceInputEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VOICE_INPUT_ENABLED] = enabled }
    }

    // ── Clear all ──
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
