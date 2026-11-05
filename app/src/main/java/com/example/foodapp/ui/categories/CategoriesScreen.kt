package com.example.foodapp.ui.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.CategoryItem
import com.example.foodapp.categories as localCategories
import com.example.foodapp.products as localProducts
import com.example.foodapp.ui.components.ProductCard

@Composable
fun CategoriesScreen(state: AppState, viewModel: CategoriesViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val displayCategories: List<CategoryItem> = if (categories.isNotEmpty()) {
        listOf(CategoryItem("all", "Tutti", "🍔")) + categories.map {
            CategoryItem(it.idCategory, it.nameCategory, "🍕")
        }
    } else {
        localCategories
    }

    val mappedProducts = if (products.isNotEmpty()) {
        products.mapNotNull { p ->
            com.example.foodapp.Product(
                id = p.IdProduct?.takeIf { it.isNotBlank() } ?: return@mapNotNull null,
                category = p.category,
                name = p.nameProduct,
                description = p.description,
                price = p.basePrice,
                emoji = "🍔"
            )
        }
    } else {
        localProducts
    }

    val filtered = if (selectedCategory == "all") mappedProducts
    else mappedProducts.filter { it.category == selectedCategory }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Menu FastBite", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(displayCategories.size) { idx ->
                    val cat = displayCategories[idx]
                    val sel = selectedCategory == cat.id
                    FilterChip(
                        selected = sel,
                        onClick = { viewModel.selectCategory(cat.id) },
                        label = { Text("${cat.emoji} ${cat.name}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
        items(filtered) { p ->
            ProductCard(p) { state.openCustomize(p) }
        }
    }
}
