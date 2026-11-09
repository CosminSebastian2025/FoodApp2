package com.example.foodapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.SERVER_ERROR_MESSAGE
import com.example.foodapp.data.model.ApiError
import com.example.foodapp.data.model.AuthRequest
import com.example.foodapp.data.model.AuthResponse
import com.example.foodapp.retrofit.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
                val res = RetrofitClient.apiService.loginUser(request = AuthRequest(email, password = pass))
                _authResult.value = res
                if (res.success) onSuccess(res) else _errorMessage.value = failureMessage(res)
            } catch (e: Exception) {
                _errorMessage.value = readableMessage(e)
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
                val res = RetrofitClient.apiService.registerUser(request = AuthRequest(email, password = pass, name = name))
                _authResult.value = res
                if (res.success) onSuccess(res) else _errorMessage.value = failureMessage(res)
            } catch (e: Exception) {
                _errorMessage.value = readableMessage(e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Svuota l'errore mostrato (es. passando da Registrati ad Accedi). */
    fun clearError() {
        _errorMessage.value = null
    }

    private fun failureMessage(res: AuthResponse): String =
        res.message.ifBlank { res.error ?: "Operazione non riuscita" }

    /** Legge il campo "error" restituito dal server sugli status HTTP di errore (es. 401). */
    private fun readableMessage(ex: Exception): String {
        if (ex is HttpException) {
            val body = ex.response()?.errorBody()?.string()
            val serverMessage = runCatching {
                Gson().fromJson(body, ApiError::class.java)?.error
            }.getOrNull()
            if (!serverMessage.isNullOrBlank()) return serverMessage
        }
        return SERVER_ERROR_MESSAGE
    }
}
