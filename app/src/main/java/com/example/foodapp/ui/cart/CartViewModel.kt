package com.example.foodapp.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.SERVER_ERROR_MESSAGE
import com.example.foodapp.data.model.CartRules
import com.example.foodapp.data.model.PaymentMethod
import com.example.foodapp.data.model.PickupSlots
import com.example.foodapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val _cartRules = MutableStateFlow<CartRules?>(null)
    val cartRules: StateFlow<CartRules?> = _cartRules

    // 2. Metodi di pagamento disponibili
    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods

    // 3. Slot orari per il ritiro dell'asporto
    private val _pickupSlots = MutableStateFlow<PickupSlots?>(null)
    val pickupSlots: StateFlow<PickupSlots?> = _pickupSlots

    // 4. Stato di caricamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 5. Gestione errori
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadCartData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            // Nessuna cache: i dati precedenti vengono scartati ad ogni caricamento
            _cartRules.value = null
            _paymentMethods.value = emptyList()
            _pickupSlots.value = null
            try {
                val rules = RetrofitClient.apiService.getCartRules()
                val payments = RetrofitClient.apiService.getPaymentMethods()
                val slots = RetrofitClient.apiService.getPickupSlots()

                _cartRules.value = rules
                _paymentMethods.value = payments
                _pickupSlots.value = slots
            } catch (e: Exception) {
                _errorMessage.value = SERVER_ERROR_MESSAGE
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
