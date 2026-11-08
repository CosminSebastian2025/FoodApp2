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

    private val _confirmation = MutableStateFlow<PickupOrderConfirmation?>(null)
//    val orderConfirmation: StateFlow<PickupOrderConfirmation?> = _confirmation

    // 2. Stato di caricamento (mostra il CircularProgressIndicator sul bottone)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 3. Gestione degli errori di rete o validazione
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun submitOrder(request: PickupOrderRequest, onSuccess: (PickupOrderConfirmation) -> Unit) {
        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {
                val conf =
                    RetrofitClient.apiService.createOrder("create_order", orderRequest = request)
                _confirmation.value = conf
                onSuccess(conf) // Esegue l'azione di successo (es. pulire il carrello e cambiare schermata)
            } catch (ex: Exception) {
                _errorMessage.value = "Errore di connessione: ${ex.message}"
                ex.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
