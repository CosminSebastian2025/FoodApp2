package com.example.foodapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.Promotion
import com.example.foodapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _promotions = MutableStateFlow<List<Promotion>>(emptyList())
    val promotions: StateFlow<List<Promotion>> = _promotions

//    private val _stores = MutableStateFlow<List<Store>>(emptyList())
//    val stores: StateFlow<List<Store>> = _stores

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
                val promoRes = try {
                    RetrofitClient.apiService.getPromotions()
                } catch (e: Exception) {
                    emptyList()
                }
                //
                val popularRes = try {
                    RetrofitClient.apiService.getPopularProducts()
                } catch (e: Exception) {
                    listOf(
                        Product(
                            "101",
                            "Fast Bacon Burger",
                            "Manzo 180g, bacon croccante, cheddar, salsa special.",
                            9.50,
                            "",
                            "burgers",
                            true
                        ),
                        Product(
                            "102",
                            "Double Cheese Monster",
                            "Doppio burger, doppio cheddar melted, cipolla caramellata.",
                            11.50,
                            "",
                            "burgers",
                            true
                        )
                    )
                }

                _promotions.value = promoRes
                //_stores.value = storeRes
                _popularProducts.value = popularRes
            } catch (e: Exception) {
                _errorMessage.value = "Modalità Offline (Server non raggiungibile)"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
