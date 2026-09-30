package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ApiKeyManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("voxdub_preferences", Context.MODE_PRIVATE)

    private val _apiKeyFlow = MutableStateFlow(getEffectiveApiKey())
    val apiKeyFlow: StateFlow<String> = _apiKeyFlow.asStateFlow()

    fun getEffectiveApiKey(): String {
        val userKey = prefs.getString(PREF_CUSTOM_API_KEY, "") ?: ""
        if (userKey.isNotBlank()) {
            return userKey
        }
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        return if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
    }

    fun getSavedCustomKey(): String {
        return prefs.getString(PREF_CUSTOM_API_KEY, "") ?: ""
    }

    fun saveApiKey(newKey: String) {
        val trimmed = newKey.trim()
        prefs.edit().putString(PREF_CUSTOM_API_KEY, trimmed).apply()
        _apiKeyFlow.value = getEffectiveApiKey()
    }

    fun clearApiKey() {
        prefs.edit().remove(PREF_CUSTOM_API_KEY).apply()
        _apiKeyFlow.value = getEffectiveApiKey()
    }

    fun isConfigured(): Boolean {
        return getEffectiveApiKey().isNotBlank()
    }

    fun getKeySource(): String {
        val userKey = prefs.getString(PREF_CUSTOM_API_KEY, "") ?: ""
        if (userKey.isNotBlank()) {
            return "Custom Key Set in App"
        }
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") {
            return "AI Studio Secrets Panel (BuildConfig)"
        }
        return "Not Configured"
    }

    fun getMaskedKey(): String {
        val key = getEffectiveApiKey()
        if (key.isBlank()) return "No API key configured"
        if (key.length <= 8) return "••••••••"
        return key.take(6) + "••••••••" + key.takeLast(4)
    }

    companion object {
        private const val PREF_CUSTOM_API_KEY = "custom_gemini_api_key"

        @Volatile
        private var INSTANCE: ApiKeyManager? = null

        fun getInstance(context: Context): ApiKeyManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApiKeyManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
