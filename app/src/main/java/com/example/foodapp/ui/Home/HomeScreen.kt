package com.example.foodapp.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
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
import com.example.foodapp.Slate200
import com.example.foodapp.Slate400
import com.example.foodapp.Slate700
import com.example.foodapp.Slate900
import com.example.foodapp.categories
import com.example.foodapp.data.model.*
import com.example.foodapp.ui.components.ProductCard
import com.example.foodapp.ui.menu.ProductCard

@Composable
fun HomeScreen(state: AppState, viewModel: HomeViewModel = viewModel()) {
    val promotions by viewModel.promotions.collectAsState()
    val stores by viewModel.stores.collectAsState()
    val popularProducts by viewModel.popularProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    val mappedPopulars = popularProducts.filter { product ->
        !product.IdProduct.isNullOrBlank()
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        errorMessage?.let { err ->
            item {
                Text(err, color = Brand, fontSize = 12.sp)
            }
        }
        // Banner punto vendita
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Slate900),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Surface(color = Brand.copy(alpha = 0.3f), shape = RoundedCornerShape(50)) {
                            Text(
                                "SOLO ASPORTO",
                                Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand.copy(alpha = 0.9f)
                            )
                        }
                        Text(
                            "📍 ${state.selectedStore.distance}",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        state.selectedStore.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(state.selectedStore.address, color = Slate400, fontSize = 11.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            "⏱ Pronto in: ${state.selectedStore.prepTime}",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                        Text("Ritiro: ", color = Slate400, fontSize = 11.sp)
                    }
                }
            }
        }
        // Categorie
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Categorie", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Text(
                        "Vedi tutte", color = Brand, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { state.screen = "categories" })
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(categories) { cat ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Slate200),
                            color = Color.White,
                            modifier = Modifier.clickable {
                                state.selectedCategory = cat.id; state.screen = "categories"
                            }
                        ) {
                            Row(
                                Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.emoji, fontSize = 16.sp)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    cat.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate700
                                )
                            }
                        }
                    }
                }
            }
        }
        // Più ordinati
        item {
            Text(
                "I Più Ordinati da Asporto",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
        }
//        val mappedPopulars = popularProducts.mapNotNull { p ->
//            val productId = p.IdProduct.takeIf { it.isNotBlank() } ?: return@mapNotNull null
//        }
        items(mappedPopulars) { p ->
            ProductCard(p) {
                state.openCustomize(p)
            }
        }
    }
}
