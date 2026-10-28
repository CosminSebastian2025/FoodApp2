package com.example.foodapp.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.ProductOptionsResponse
import com.example.foodapp.data.repository.FastBiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val options: ProductOptionsResponse? = null,
    // MAP: groupId -> Set<choiseId>
    val selectedOptions: Map<String, Set<String>> = emptyMap(),
    val quantity: Int = 1,
    val error: String? = null
)

class ProductDetailViewModel( private val productId: String, private val repo: FastBiteRepository ): ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init { loadProduct() }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val product = repo.getProductById(productId) ?: throw Exception("Prodotto non trovato")
                val options = repo.getProductOptions(productId) ?: ProductOptionsResponse(productId, emptyList())

                //Preseleziona la prima opzione per i gruppi "single" obbligatori
                val preselected = options.groups.filter {
                    it.selectionType == "single" && it.choises.isNotEmpty()
                }.associate {
                    it.groupId to setOf(it.choises.first().id)
                }

                _uiState.value = ProductDetailUiState(
                    isLoading = false,
                    product = product,
                    options = options,
                    selectedOptions = preselected
                )
            }
            catch (e: Exception) {
                _uiState.value = uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun selectSingle(groupId: String, choiseId: String) {
        val current = _uiState.value.selectedOptions.toMutableMap()
        current[groupId] = setOf(choiseId)
        _uiState.value = _uiState.value.copy(selectedOptions = current)
    }

    /***
     * Restituisce più opzioni di un prodotto che l'utente digita
     */

    fun toggleMultiple(groupId: String, choiseId: String) {
        val current = _uiState.value.selectedOptions.toMutableMap()
        val selected = current[choiseId]?.toMutableSet() ?: mutableSetOf()
        if (selected.contains(choiseId)) selected.remove(choiseId)
        else selected.add(choiseId)
        current[groupId] = selected
        _uiState.value = _uiState.value.copy(selectedOptions = current)
    }

    /***
     * Calcola la quantita di un prodotto o più prodotto selezionati
     */

    fun changeQuantity(delta: Int) {
        val newQt = (_uiState.value.quantity + delta).coerceIn(1, 20)
        _uiState.value = _uiState.value.copy(quantity = newQt)
    }

    /***
     * Calcola il prezzo unitario considerandp base + modifiche opzioni
     */
    fun computeUnitPrice(): Double {
        val product = _uiState.value.product ?: return 0.0
        val options = _uiState.value.options ?: return product.basePrice.toDouble()
        var modifier = 0.00
        for (group in options.groups) {
            val selected = _uiState.value.selectedOptions[group.groupId] ?: emptySet()
            for (choise in group.choises) {
                if (choise.id in selected) modifier += choise.priceModifier
            }
        }

        return product.basePrice + modifier
    }

    fun computeTotal(): Double = computeUnitPrice() * _uiState.value.quantity

    /**
     * Verifica che tutti i gruppi obbligatori abbiano almeno una scelta
     */

    fun isSelectionValid(): Boolean {
        val options = _uiState.value.options ?: return false
        return options.groups
            .filter { it.isRequired }
            .all { group -> val selected = _uiState.value.selectedOptions[group.groupId] ?: emptySet()
                selected.isNotEmpty()
            }
    }

    /**
     * Costruisce il CartItem da aggiungere al carrello.
     */
    fun buildCartItem(): CartItem? {
        val product = _uiState.value.product ?:return null
        val state = _uiState.value
        val selectedIds = state.selectedOptions.values.flatten()
        return CartItem(
            productId = product.IdProduct,
            productName = product.nameProduct,
            basePrice = product.basePrice.toDouble(),
            quantity = state.quantity,
            selectedOptions = selectedIds,
            optionPriceModifier = computeUnitPrice() - product.basePrice
        )
    }
}

class ProductDetailViewModelFactory(
    private val productId: String,
    private val repo: FastBiteRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProductDetailViewModel(productId, repo) as T
    }
}