package com.example.foodapp.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.retrofit.RetrofitClient
import com.example.foodapp.data.model.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoreViewModel : ViewModel() {
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadStores() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val res = RetrofitClient.apiService.getStores()
                _stores.value = res
            } catch (e: Exception) {
                _errorMessage.value = "Errore caricamento negozi: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
