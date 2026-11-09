package com.example.foodapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.SERVER_ERROR_MESSAGE
import com.example.foodapp.retrofit.RetrofitClient
import com.example.foodapp.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            // Nessuna cache: i risultati precedenti vengono scartati
            _searchResults.value = emptyList()
            try {
                val results = RetrofitClient.apiService.searchProducts(query = query)
                _searchResults.value = results
            } catch (e: Exception) {
                _errorMessage.value = SERVER_ERROR_MESSAGE
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Svuota risultati ed errori quando il campo di ricerca viene svuotato. */
    fun clear() {
        _searchResults.value = emptyList()
        _errorMessage.value = null
    }
}
