package com.example.foodapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.OrderHistoryItemResponse
import com.example.foodapp.data.model.PaymentHistoryItemResponse
import com.example.foodapp.data.model.UserProfile
import com.example.foodapp.retrofit.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _orderHistory = MutableStateFlow<List<OrderHistoryItemResponse>>(emptyList())
    val orderHistory: StateFlow<List<OrderHistoryItemResponse>> = _orderHistory

    private val _paymentHistory = MutableStateFlow<List<PaymentHistoryItemResponse>>(emptyList())
    val paymentHistory: StateFlow<List<PaymentHistoryItemResponse>> = _paymentHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadProfileData(userId: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val profile = RetrofitClient.apiService.getUserProfile(userId)
                val orders = RetrofitClient.apiService.getOrderHistory(userId)
                val payments = RetrofitClient.apiService.getPaymentHistory(userId)

                _userProfile.value = profile
                _orderHistory.value = orders
                _paymentHistory.value = payments
            } catch (e: Exception) {
                _errorMessage.value = "Errore caricamento profilo: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
