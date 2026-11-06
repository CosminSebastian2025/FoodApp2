package com.example.foodapp.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.PickupOrderConfirmation
import com.example.foodapp.data.model.PickupOrderRequest
import com.example.foodapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {
    private val _orderConfirmation = MutableStateFlow<PickupOrderConfirmation?>(null)
    val orderConfirmation: StateFlow<PickupOrderConfirmation?> = _orderConfirmation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun submitOrder(request: PickupOrderRequest, onSuccess: (PickupOrderConfirmation) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.apiService.createOrder(orderRequest = request)
                _orderConfirmation.value = response
                onSuccess(response)
            } catch (e: Exception) {
                _errorMessage.value = "Errore invio ordine: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
