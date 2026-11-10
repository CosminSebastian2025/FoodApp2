package com.example.foodapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.SERVER_ERROR_MESSAGE
import com.example.foodapp.retrofit.RetrofitClient
import com.example.foodapp.data.model.Product
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var searchJob: Job? = null

    /**
     * Scarica i prodotti dal server e filtra localmente per nome, descrizione
     * e categoria. Il termine vuoto mostra l'intero menu.
     */
    fun search(query: String) {
        searchJob?.cancel()
        val term = query.trim()

        searchJob = viewModelScope.launch {
            try {
                delay(300)
                _isLoading.value = true
                _errorMessage.value = null
                _searchResults.value = emptyList()

                val allProducts = RetrofitClient.apiService.getProducts()
                _searchResults.value = if (term.isBlank()) {
                    allProducts
                } else {
                    allProducts.filter { product ->
                        product.name.contains(term, ignoreCase = true) ||
                                product.description.contains(term, ignoreCase = true) ||
                                product.allCategories.any { it.contains(term, ignoreCase = true) }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage.value = SERVER_ERROR_MESSAGE
                e.printStackTrace()
            } finally {
                // Un job annullato non deve spegnere il loading della nuova ricerca.
                if (isActive) _isLoading.value = false
            }
        }
    }

    /** Svuota risultati ed errori quando il campo di ricerca viene svuotato. */
    fun clear() {
        searchJob?.cancel()
        _searchResults.value = emptyList()
        _errorMessage.value = null
        _isLoading.value = false
    }
}
