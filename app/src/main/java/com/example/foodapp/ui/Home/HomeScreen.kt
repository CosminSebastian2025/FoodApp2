package com.example.foodapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import coil3.compose.AsyncImage
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.Promotion
import com.example.foodapp.data.repository.FastBiteRepository
import com.example.foodapp.ui.Theme.FastBiteGray
import com.example.foodapp.ui.Theme.FastBiteRed
import com.example.foodapp.ui.Theme.FastBiteTextGray
import com.example.foodapp.ui.Theme.FastBiteTheme
import com.example.foodapp.ui.Theme.FastBiteYellow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ===== VIEWMODEL =====
data class HomeUiState(
    val isLoading: Boolean = true,
    val promotions: List<Promotion> = emptyList(),
    val categories: List<Category> = emptyList(),
    val popularProducts: List<Product> = emptyList(),
    val error: String? = null
)

class HomeViewModel(private val repo: FastBiteRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    promotions = repo.getPromotions(),
                    categories = repo.getCategories(),
                    popularProducts = repo.getPopularProducts()
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(isLoading = false, error = e.message)
            }
        }
    }
}

class HomeViewModelFactory(private val repo: FastBiteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repo) as T
}

// ===== SCREEN =====
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: HomeViewModel,
    onProductClick: (String) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("FastBite", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = FastBiteRed)
                        Spacer(Modifier.width(8.dp))
                        Surface(shape = CircleShape, color = FastBiteYellow) {
                            Text("🔥", modifier = Modifier.padding(4.dp), fontSize = 14.sp)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, "Cerca") }
                    IconButton(onClick = {}) { Icon(Icons.Default.Person, "Profilo") }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FastBiteRed)
            }
            state.error != null -> Box(Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Errore: ${state.error}")
                    Button(onClick = { vm.loadData() }) { Text("Riprova") }
                }
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(2) }) { PromotionBanner(state.promotions.firstOrNull()) }
                item(span = { GridItemSpan(2) }) { CategoriesSection(state.categories, onCategoryClick) }
                item(span = { GridItemSpan(2) }) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("I Più Ordinati da Asporto", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        TextButton(onClick = {}) { Text("Vedi tutti") }
                    }
                }
                items(state.popularProducts) { p -> ProductCard(p) { onProductClick(p.IdProduct) } }
            }
        }
    }
}

@Composable
private fun PromotionBanner(promo: Promotion?) {
    promo ?: return
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp), shape = RoundedCornerShape(16.dp)) {
        Box {
            AsyncImage(
                model = promo.ImageUrl, contentDescription = null,
                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                    )
                ))
            Column(Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)) {
                Surface(color = FastBiteRed, shape = RoundedCornerShape(6.dp)) {
                    Text("Solo Asporto", color = Color.White, fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(6.dp))
                Text(promo.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text(promo.subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, maxLines = 2)
            }
        }
    }
}

@Composable
private fun CategoriesSection(categories: List<Category>, onClick: (String) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Categorie", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            TextButton(onClick = {}) { Text("Vedi tutte") }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(categories) { cat ->
                Column(
                    Modifier
                        .width(72.dp)
                        .clickable { onClick(cat.idCategory) },
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(shape = CircleShape, color = FastBiteGray, modifier = Modifier.size(60.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text(cat.icon, fontSize = 28.sp) }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(cat.nameCategory, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("${cat.count} piatti", fontSize = 10.sp, color = FastBiteTextGray)
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            Box {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.nameProduct,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp), contentScale = ContentScale.Crop)
                if (product.isPopular) Surface(color = FastBiteYellow,
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)) {
                    Text("⭐ Popolare", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }
            Column(Modifier.padding(10.dp)) {
                Text(
                    product.nameProduct, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text("€ ${"%.2f".format(product.basePrice)}", color = FastBiteRed,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// ===== PREVIEW =====
@Preview(showBackground = true, showSystemUi = true, name = "Home - Caricamento")
@Composable
private fun HomeScreenLoadingPreview() {
    FastBiteTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = FastBiteRed)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home - Banner")
@Composable
private fun HomeBannerPreview() {
    FastBiteTheme {
        PromotionBanner(Promotion("p1", "Ritiro in negozio",
            "Ordina in app. Salta la fila.",
            listOf("Zero costi", "QR rapido"),
            "https://images.unsplash.com/photo-1561758033-d89a9ad46330?w=800"))
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home - Categorie")
@Composable
private fun HomeCategoriesPreview() {
    FastBiteTheme {
        CategoriesSection(listOf(
            Category("c1", "Burger", "🍔", 12),
            Category("c2", "Pollo", "🍗", 8),
            Category("c3", "Contorni", "🍟", 15)
        ), {})
    }
}

@Preview(showBackground = true, name = "Home - Product Card")
@Composable
private fun HomeProductCardPreview() {
    FastBiteTheme {
        ProductCard(Product("p1", "Big Mac", "Descrizione", 5.99,
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400",
            "cat_burger", true), {})
    }
}

// Repo fittizio per preview
//private class FakeRepoForPreview : FastBiteRepository(
//    AssetsJsonReader(
//        LocalContext.current.let {
//            throw UnsupportedOperationException()
//        }
//    ) {
//        override fun getPromotions(): List<Promotion> = emptyList()
//    }
//)