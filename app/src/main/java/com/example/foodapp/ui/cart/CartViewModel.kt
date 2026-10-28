package com.example.foodapp.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CartRules
import com.example.foodapp.data.repository.FastBiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartTotals(
    val subtotal: Double,
    val discount: Double,
    val managementFee: Double,
    val pickupFee: Double,
    val total: Double
)

class CartViewModel(private val repo: FastBiteRepository): ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    private val _coupon = MutableStateFlow<String?>(null)
    val coupon: StateFlow<String?> = _coupon.asStateFlow()

    private val _discountPercent = MutableStateFlow(0)
    val discountPercent: StateFlow<Int> = _discountPercent.asStateFlow()

    private val _rules = MutableStateFlow<CartRules?>(null)
    val rules: StateFlow<CartRules?> = _rules.asStateFlow()

    val itemCount: StateFlow<Int> = MutableStateFlow(0).also { flow ->
        _items. collect { list ->
            (flow as MutableStateFlow<Int>).value = list.sumOf { it.quantity } }
    }

    init {
        viewModelScope.launch {
            _rules.value = repo.getCartRules()
        }
    }

    fun addItem(item: CartItem) {
        _items.value = _items.value + item
    }

    fun updateQuantity(index: Int, delta: Int) {
        val list = _items.value.toMutableList()
        if (index !in list.indices) return
        val newQt = list[index].quantity + delta
        if (newQt <= 0) list.removeAt(index)
        else list[index] = list[index].copy(quantity = newQt)
        _items.value = list
    }

    fun removeItem(index: Int) {
        val list = _items.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _items.value = list
        }
    }

    fun clearCart() {
        _items.value = emptyList()
        _coupon.value = null
        _discountPercent.value = 0
    }

    suspend fun applyCoupon(code: String): Result<Int> {
        return try {
            val rules = _rules.value ?: return Result.failure(Exception("Regole non caricate"))
            if (code.uppercase() == rules.discountCode) {
                _coupon.value = code.uppercase()
                _discountPercent.value = rules.discountCode
                Result.success(rules.discountPercent)
            } else {
                Result.failure(Exception("Codice non valido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun removeCoupon() {
        _coupon.value = null
        _discountPercent.value = 0
    }

    fun calculateTotal(): CartTotals {
        val r = rules.value ?: return CartTotals(0.0,0.0,0.0,0.0,0.0)
        val subtotal = _items.value.sumOf { it.totalPrice }
        val discount = subtotal * _discountPercent.value / 100.0
        val total = subtotal - discount + r.managementFee + r.pickupFee
        return CartTotals(
            subtotal = subtotal,
            discount = discount,
            managementFee = r.managementFee,
            pickupFee = r.pickupFee,
            total = total
        )
    }
}