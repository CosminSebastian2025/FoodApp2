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

    // 1. Stato del profilo utente
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    // 2. Stato dello storico ordini
    private val _orderHistory = MutableStateFlow<List<OrderHistoryItemResponse>>(emptyList())
    val orderHistory: StateFlow<List<OrderHistoryItemResponse>> = _orderHistory

    // 3. Stato dello storico pagamenti
    private val _paymentHistory = MutableStateFlow<List<PaymentHistoryItemResponse>>(emptyList())
    val paymentHistory: StateFlow<List<PaymentHistoryItemResponse>> = _paymentHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadProfileData(userId: String?, forceRefresh: Boolean = false) {

        if (userId.isNullOrBlank()) return

        // Evita di ricaricare se i dati sono già presenti in memoria
        if (!forceRefresh && _userProfile.value != null && _orderHistory.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val usrProf = RetrofitClient.apiService.getUserProfile(userId)
                val ordHst = RetrofitClient.apiService.getOrderHistory(userId)
                val pmnHst = RetrofitClient.apiService.getPaymentHistory(userId)

                _userProfile.value = usrProf
                _orderHistory.value = ordHst
                _paymentHistory.value = pmnHst

            } catch (e: Exception) {
                _errorMessage.value = "Errore caricamento profilo: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
