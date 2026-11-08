package com.example.foodapp.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.AppState
import com.example.foodapp.Brand
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

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Brand)
        }
        return
    }

    // Filtraggio dei prodotti in base alla categoria selezionata
    val filteredProducts = if (selectedCategory == "all") {
        products.filter { !it.IdProduct.isNullOrBlank() }
    } else {
        products.filter {
            !it.IdProduct.isNullOrBlank() &&
                    it.category.equals(selectedCategory, ignoreCase = true)
        }
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
            // LazyRow dei filtri per Categoria
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Chip "Tutti"
                item {
                    FilterChip(
                        selected = selectedCategory == "all",
                        onClick = { viewModel.selectCategory("all") },
                        label = { Text("🍔 Tutti", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                items(categories) { cat ->
                    val catId = cat.idCategory
                    val sel = selectedCategory == catId
                    FilterChip(
                        selected = sel,
                        onClick = { viewModel.selectCategory(catId) },
                        label = {
                            Text(
                                "${cat.icon} ${cat.nameCategory}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
        // Mostra i prodotti filtrati per categoria
        items(filteredProducts) { p ->
            ProductCard(p) { state.openCustomize(p) }
        }
    }
}
