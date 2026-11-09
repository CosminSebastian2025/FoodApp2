package com.example.foodapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.SERVER_ERROR_MESSAGE
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.Promotion
import com.example.foodapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // 1. Stato privato modificabile
    private val _categorie = MutableStateFlow<List<Category>>(emptyList())

    // Stato pubblico di sola lettura per la UI (Compose)
    val categorie: StateFlow<List<Category>> = _categorie

    private val _promozioni = MutableStateFlow<List<Promotion?>>(emptyList())
    val promozioni: StateFlow<List<Promotion?>> = _promozioni

    private val _popolari = MutableStateFlow<List<Product?>>(emptyList())
    val popolari: StateFlow<List<Product?>> = _popolari

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 2. Funzione per caricare i dati (es. da Retrofit)
    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            // Nessuna cache: i dati precedenti vengono scartati ad ogni caricamento
            _categorie.value = emptyList()
            _promozioni.value = emptyList()
            _popolari.value = emptyList()
            try {
                // Chiamata di rete sospesa
                val prom = RetrofitClient.apiService.getPromotions()
                val productPopular = RetrofitClient.apiService.getPopularProducts()
                val cat = RetrofitClient.apiService.getCategories()
                _categorie.value = cat
                _promozioni.value = prom
                _popolari.value = productPopular
            } catch (e: Exception) {
                _errorMessage.value = SERVER_ERROR_MESSAGE
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
