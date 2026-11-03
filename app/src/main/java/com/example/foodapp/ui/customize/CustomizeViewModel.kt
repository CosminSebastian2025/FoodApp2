package com.example.foodapp.ui.customize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.retrofit.RetrofitClient
import com.example.foodapp.data.model.ProductOptionsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomizeViewModel : ViewModel() {
    private val _optionsResponse = MutableStateFlow<List<ProductOptionsResponse>>(emptyList())
    val optionsResponse: StateFlow<List<ProductOptionsResponse>> = _optionsResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadOptions() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val res = RetrofitClient.apiService.getProductOptionsResponse()
                _optionsResponse.value = res
            } catch (e: Exception) {
                _errorMessage.value = "Errore caricamento opzioni: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
