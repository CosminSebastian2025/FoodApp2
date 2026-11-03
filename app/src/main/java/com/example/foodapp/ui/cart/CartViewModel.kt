package com.example.foodapp.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.Retrofit.RetrofitClient
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.PickupOrderConfirmation
import com.example.foodapp.data.model.PickupOrderRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel: ViewModel() {

    //Simuliamo un carrello con alcuni articoli
    private val _cartItem = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItem: StateFlow<List<CartItem>> = _cartItem

    private val _isCheckingOut = MutableStateFlow(false)
    val isCheckingOut: StateFlow<Boolean> = _isCheckingOut

    private val _orderConfirmation = MutableStateFlow<PickupOrderConfirmation?>(null)
    val orderConfirmation: StateFlow<PickupOrderConfirmation?> = _orderConfirmation

    fun checkoutOrder(storeId: String, userId: String = "user_123") {
        viewModelScope.launch {
            _isCheckingOut.value = true
            try {
                val items = _cartItem.value
                val subTotal = items.sumOf { it.totalPrice }

                // Costruiamo l'ogetto che Retrofit trasformerà in JSON

                val orderRequest = PickupOrderRequest(
                    userId = userId,
                    storeId = storeId,
                    pickupType = "immediate",
                    scheduledTime = null,
                    discountCode = null,
                    items = items,
                    subTotal = subTotal,
                    discount = 0.00,
                    managementFee = 1.50, //Esempio fisso
                    total = subTotal + 1.50
                )

                val response = RetrofitClient.apiService.createOrder("create_order", orderRequest)
                _orderConfirmation.value = response
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                _isCheckingOut.value = false
            }
        }
    }

}