package com.example.foodapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.AuthRequest
import com.example.foodapp.data.model.AuthResponse
import com.example.foodapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authResult = MutableStateFlow<AuthResponse?>(null)
    val authResult: StateFlow<AuthResponse?> = _authResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(email: String, pass: String, onSuccess: (AuthResponse) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val res = RetrofitClient.apiService.loginUser(request = AuthRequest(email, pass))
                _authResult.value = res
                if (res.success) onSuccess(res)
                else _errorMessage.value = res.message
            } catch (e: Exception) {
                _errorMessage.value = "Errore login: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, pass: String, name: String, onSuccess: (AuthResponse) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val res = RetrofitClient.apiService.registerUser(request = AuthRequest(email, pass, name))
                _authResult.value = res
                if (res.success) onSuccess(res)
                else _errorMessage.value = res.message
            } catch (e: Exception) {
                _errorMessage.value = "Errore registrazione: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
