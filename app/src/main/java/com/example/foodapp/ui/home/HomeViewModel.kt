package com.example.foodapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.Promotion
import com.example.foodapp.data.repository.FastBiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUIState(
    val isLoading: Boolean = true,
    val promotions: List<Promotion> = emptyList(),
    val category: List<Category> = emptyList(),
    val popularProducts: List<Product> = emptyList(),
    val error: String? = null
)

class HomeViewModel(private val repo: FastBiteRepository): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()


    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val promos = repo.getPromotions()
                val cats = repo.getCategories()
                val popular = repo.getPopularProducts()
                _uiState.value = HomeUIState(
                    isLoading = false,
                    promotions = promos,
                    category = cats,
                    popularProducts = popular
                )
            }
            catch (e: Exception)
            {
                _uiState.value = HomeUIState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

class HomeViewModelFactory(private val repo: FastBiteRepository): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(repo) as T
    }
}