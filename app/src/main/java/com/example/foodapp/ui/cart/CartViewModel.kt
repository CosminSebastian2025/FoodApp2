package com.example.foodapp.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.retrofit.RetrofitClient
import com.example.foodapp.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel: ViewModel() {

    private val _cartItem = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItem: StateFlow<List<CartItem>> = _cartItem

    private val _cartRules = MutableStateFlow<CartRules?>(null)
    val cartRules: StateFlow<CartRules?> = _cartRules

    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods

    private val _pickupSlots = MutableStateFlow<PickupSlots?>(null)
    val pickupSlots: StateFlow<PickupSlots?> = _pickupSlots

    private val _isCheckingOut = MutableStateFlow(false)
    val isCheckingOut: StateFlow<Boolean> = _isCheckingOut

    private val _orderConfirmation = MutableStateFlow<PickupOrderConfirmation?>(null)
    val orderConfirmation: StateFlow<PickupOrderConfirmation?> = _orderConfirmation

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadCartData() {
        viewModelScope.launch {
            try {
                val rules = RetrofitClient.apiService.getCartRules()
                val payments = RetrofitClient.apiService.getPaymentMethods()
                val slots = RetrofitClient.apiService.getPickupSlots()

                _cartRules.value = rules
                _paymentMethods.value = payments
                _pickupSlots.value = slots
            } catch (e: Exception) {
                _errorMessage.value = "Errore caricamento carrello/checkout: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun checkoutOrder(storeId: String, userId: String = "user_123") {
        viewModelScope.launch {
            _isCheckingOut.value = true
            _errorMessage.value = null
            try {
                val items = _cartItem.value
                val subTotal = items.sumOf { it.totalPrice }
                val mgmtFee = _cartRules.value?.managementFee ?: 1.50

                val orderRequest = PickupOrderRequest(
                    userId = userId,
                    storeId = storeId,
                    pickupType = "immediate",
                    scheduledTime = null,
                    discountCode = null,
                    items = items,
                    subTotal = subTotal,
                    discount = 0.00,
                    managementFee = mgmtFee,
                    total = subTotal + mgmtFee
                )

                val response = RetrofitClient.apiService.createOrder("create_order", orderRequest)
                _orderConfirmation.value = response
            }
            catch (e: Exception) {
                _errorMessage.value = "Errore creazione ordine: ${e.message}"
                e.printStackTrace()
            }
            finally {
                _isCheckingOut.value = false
            }
        }
    }

}
