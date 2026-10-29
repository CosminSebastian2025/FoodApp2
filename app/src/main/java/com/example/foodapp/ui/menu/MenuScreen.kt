package com.fastbite.app.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.fastbite.app.data.model.Category
import com.fastbite.app.data.model.Product
import com.fastbite.app.data.repository.FastBiteRepository
import com.fastbite.app.ui.theme.FastBiteRed
import com.fastbite.app.ui.theme.FastBiteTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MenuUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedCategory: String? = null
)

class MenuViewModel(private val repo: FastBiteRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val cats = repo.getCategories()
            val all = repo.getAllProducts()
            _uiState.value = MenuUiState(isLoading = false, categories = cats, products = all)
        }
    }

    fun selectCategory(catId: String?) {
        viewModelScope.launch {
            val all = repo.getAllProducts()
            val filtered = if (catId == null) all else all.filter { it.category == catId }
            _uiState.value = _uiState.value.copy(selectedCategory = catId, products = filtered)
        }
    }
}

class MenuViewModelFactory(private val repo: FastBiteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MenuViewModel(repo) as T
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(vm: MenuViewModel, onProductClick: (String) -> Unit = {}) {
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Menu Completo", fontWeight = FontWeight.Bold) })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Filtro categorie
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.selectedCategory == null,
                        onClick = { vm.selectCategory(null) },
                        label = { Text("Tutti") }
                    )
                }
                items(state.categories) { cat ->
                    FilterChip(
                        selected = state.selectedCategory == cat.id,
                        onClick = { vm.selectCategory(cat.id) },
                        label = { Text("${cat.icon} ${cat.name}") }
                    )
                }
            }

            // Griglia prodotti
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.products) { p ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { onProductClick(p.id) },
                        shape = RoundedCornerShape(12.dp)) {
                        Column {
                            AsyncImage(model = p.imageUrl, contentDescription = p.name,
                                modifier = Modifier.fillMaxWidth().height(110.dp),
                                contentScale = ContentScale.Crop)
                            Column(Modifier.padding(10.dp)) {
                                Text(p.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(Modifier.height(4.dp))
                                Text("€ ${"%.2f".format(p.basePrice)}", color = FastBiteRed,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===== PREVIEW =====
@Preview(showBackground = true, showSystemUi = true, name = "Menu - Completo")
@Composable
private fun MenuScreenPreview() {
    FastBiteTheme {
        // Preview statico
        Column {
            LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)) {
                item { FilterChip(selected = true, onClick = {}, label = { Text("Tutti") }) }
                item { FilterChip(selected = false, onClick = {}, label = { Text("🍔 Burger") }) }
                item { FilterChip(selected = false, onClick = {}, label = { Text("🍗 Pollo") }) }
            }
            LazyVerticalGrid(columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(4) { i ->
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Column {
                            Surface(Modifier.fillMaxWidth().height(110.dp), color = Color(0xFFEEEEEE)) {}
                            Column(Modifier.padding(10.dp)) {
                                Text("Prodotto $i", fontWeight = FontWeight.SemiBold)
                                Text("€ 5.99", color = FastBiteRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}