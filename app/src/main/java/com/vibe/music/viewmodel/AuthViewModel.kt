package com.vibe.music.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibe.music.data.remote.NetworkResult
import com.vibe.music.data.remote.VibeApiClient
import com.vibe.music.data.remote.VibeNetworkConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUser(
    val id: String,
    val email: String,
    val name: String,
    val role: String
)

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: AuthUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val apiClient: VibeApiClient = VibeApiClient()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // If a token already exists in prefs, verify it with /auth/me
        if (VibeNetworkConfig.authToken != null) {
            verifyToken()
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    private fun verifyToken() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = apiClient.getMe()) {
                is NetworkResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is NetworkResult.Error -> {
                    // Token is stale — clear it and go back to login
                    VibeNetworkConfig.clearAuth()
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password are required")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = apiClient.login(email.trim(), password)) {
                is NetworkResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is NetworkResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
            _isLoading.value = false
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }
        if (password.length < 8) {
            _authState.value = AuthState.Error("Password must be at least 8 characters")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = apiClient.register(name.trim(), email.trim(), password)) {
                is NetworkResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is NetworkResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            apiClient.logout()
            VibeNetworkConfig.clearAuth()
            _authState.value = AuthState.Unauthenticated
            _isLoading.value = false
        }
    }

    /** Call after navigating away from an error so it doesn't re-show on recompose. */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}
