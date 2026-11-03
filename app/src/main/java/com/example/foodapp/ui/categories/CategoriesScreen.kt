package com.example.foodapp.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.foodapp.Slate400
import com.example.foodapp.ui.components.ProductCard

@Composable
fun CategoriesScreen(state: AppState, viewModel: CategoriesViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val mappedProducts = products.map { p ->
        com.example.foodapp.Product(
            id = p.IdProduct.hashCode(),
            category = p.category,
            name = p.nameProduct,
            description = p.description,
            price = p.basePrice,
            emoji = "🍔"
        )
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        errorMessage?.let { err ->
            item {
                Text(err, color = Brand, fontSize = 12.sp)
            }
        }
        item {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Menu Completo", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text("${filtered.size} articoli", color = Slate400, fontSize = 11.sp)
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == "all",
                        onClick = { viewModel.selectCategory("all") },
                        label = { Text("Tutti", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand, selectedLabelColor = Color.White
                        )
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat.idCategory,
                        onClick = { viewModel.selectCategory(cat.idCategory) },
                        label = { Text("${cat.icon} ${cat.nameCategory}", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand, selectedLabelColor = Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        items(filtered) { p -> ProductCard(p, onCustomize = { state.openCustomize(p) }) }
    }
}
