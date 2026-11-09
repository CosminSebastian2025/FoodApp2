package com.example.foodapp.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.SERVER_ERROR_MESSAGE
import com.example.foodapp.data.model.ApiError
import com.example.foodapp.data.model.PickupOrderConfirmation
import com.example.foodapp.data.model.PickupOrderRequest
import com.example.foodapp.retrofit.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CheckoutViewModel : ViewModel() {

    private val _confirmation = MutableStateFlow<PickupOrderConfirmation?>(null)

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
                onSuccess(conf)
            } catch (ex: Exception) {
                _errorMessage.value = readableMessage(ex)
                ex.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Mostra il messaggio del server (es. dati mancanti) senza esporre dettagli SQL. */
    private fun readableMessage(ex: Exception): String {
        if (ex is HttpException) {
            val body = ex.response()?.errorBody()?.string()
            val serverMessage = runCatching {
                Gson().fromJson(body, ApiError::class.java)?.error
            }.getOrNull()

            if (!serverMessage.isNullOrBlank()) {
                return if (serverMessage.contains("SQLSTATE", ignoreCase = true)) {
                    "Il server non è riuscito a creare l'ordine. Riprova più tardi."
                } else {
                    serverMessage
                }
            }
        }
        return SERVER_ERROR_MESSAGE
    }
}
