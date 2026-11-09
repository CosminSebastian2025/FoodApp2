package com.example.foodapp.ui.customize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.SERVER_ERROR_MESSAGE
import com.example.foodapp.data.model.ProductOptionsResponse
import com.example.foodapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomizeViewModel : ViewModel() {
    // 1. Stato delle opzioni di personalizzazione del prodotto
    private val _optionsResponse = MutableStateFlow<List<ProductOptionsResponse>>(emptyList())
    val optionsResponse: StateFlow<List<ProductOptionsResponse>> = _optionsResponse

    // 2. Stato di caricamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 3. Gestione errori
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 4. Funzione per caricare le opzioni dal server (nessuna cache in memoria)
    fun loadOptions() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _optionsResponse.value = emptyList()
            try {
                val res = RetrofitClient.apiService.getProductOptionsResponse()
                _optionsResponse.value = res
            } catch (e: Exception) {
                _errorMessage.value = SERVER_ERROR_MESSAGE
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
