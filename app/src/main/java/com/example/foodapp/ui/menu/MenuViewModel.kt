package com.example.foodapp.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.SERVER_ERROR_MESSAGE
import com.example.foodapp.data.model.Product
import com.example.foodapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val _prodotti = MutableStateFlow<List<Product>>(emptyList())
    val prodotti: StateFlow<List<Product>> = _prodotti

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            // Nessuna cache: i dati precedenti vengono scartati
            _prodotti.value = emptyList()
            try {
                val response = RetrofitClient.apiService.getProducts()
                _prodotti.value = response
            } catch (e: Exception) {
                _errorMessage.value = SERVER_ERROR_MESSAGE
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
