package com.vibe.music.data.remote

import android.content.Context
import android.content.SharedPreferences

object VibeNetworkConfig {
    private const val PREFS_NAME = "vibe_network_prefs"
    private const val KEY_BASE_URL = "base_url"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USE_REMOTE = "use_remote_backend"

    // Default: 10.0.2.2 connects to host localhost from Android emulator
    const val DEFAULT_EMULATOR_URL = "http://10.0.2.2:4000/api/v1/"
    const val DEFAULT_LOCAL_URL = "http://127.0.0.1:4000/api/v1/"
    const val DEFAULT_PUBLIC_URL = "https://vibe-backend-qeil.onrender.com/api/v1/"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var baseUrl: String
        get() = prefs?.getString(KEY_BASE_URL, DEFAULT_PUBLIC_URL) ?: DEFAULT_PUBLIC_URL
        set(value) {
            val formatted = if (value.endsWith("/")) value else "$value/"
            prefs?.edit()?.putString(KEY_BASE_URL, formatted)?.apply()
        }

    var authToken: String?
        get() = prefs?.getString(KEY_AUTH_TOKEN, null)
        set(value) {
            prefs?.edit()?.putString(KEY_AUTH_TOKEN, value)?.apply()
        }

    var isRemoteEnabled: Boolean
        get() = prefs?.getBoolean(KEY_USE_REMOTE, true) ?: true
        set(value) {
            prefs?.edit()?.putBoolean(KEY_USE_REMOTE, value)?.apply()
        }

    fun clearAuth() {
        prefs?.edit()?.remove(KEY_AUTH_TOKEN)?.apply()
    }
}
