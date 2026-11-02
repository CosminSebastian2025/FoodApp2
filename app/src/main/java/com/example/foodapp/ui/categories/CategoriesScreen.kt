package com.example.foodapp.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Slate400
import com.example.foodapp.categories
import com.example.foodapp.products
import com.example.foodapp.ui.components.ProductCard

@Composable
fun CategoriesScreen(state: AppState) {
    val filtered = if (state.selectedCategory == "all") products
    else products.filter { it.category == state.selectedCategory }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                        selected = state.selectedCategory == "all",
                        onClick = { state.selectedCategory = "all" },
                        label = { Text("Tutti", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand, selectedLabelColor = Color.White
                        )
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = state.selectedCategory == cat.id,
                        onClick = { state.selectedCategory = cat.id },
                        label = { Text("${cat.emoji} ${cat.name}", fontSize = 11.sp) },
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
