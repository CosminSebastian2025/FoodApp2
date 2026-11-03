package com.example.foodapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.retrofit.RetrofitClient
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.Promotion
import com.example.foodapp.data.model.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _promotions = MutableStateFlow<List<Promotion>>(emptyList())
    val promotions: StateFlow<List<Promotion>> = _promotions

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    private val _popularProducts = MutableStateFlow<List<Product>>(emptyList())
    val popularProducts: StateFlow<List<Product>> = _popularProducts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val promoRes = RetrofitClient.apiService.getPromotions()
                val storeRes = RetrofitClient.apiService.getStores()
                val popularRes = RetrofitClient.apiService.getPopularProducts()

                _promotions.value = promoRes
                _stores.value = storeRes
                _popularProducts.value = popularRes
            } catch (e: Exception) {
                _errorMessage.value = "Errore: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
