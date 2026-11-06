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
import com.example.foodapp.ui.components.ProductCard

@Composable
fun HomeScreen(state: AppState, viewModel: HomeViewModel = viewModel()) {
    val promotions by viewModel.promotions.collectAsState()
    val popularProducts by viewModel.popularProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
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
        // 2. Inseriamo il LazyRow orizzontale dentro un item della LazyColumn
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "PROMOZIONI IN EVIDENZA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )

                // 3. Scroll Orizzontale delle promozioni
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(promotions) { promo ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Slate900),
                            modifier = Modifier
                                .width(280.dp)
                                .height(140.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (!promo.badges.isNullOrEmpty()) {
                                    Surface(
                                        color = Brand.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = promo.badges.first(),
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 3.dp
                                            ),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Brand
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = promo.title,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = promo.subtitle,
                                        color = Slate400,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
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
        items(popularProducts) { p ->
            ProductCard(p) {
                state.openCustomize(p)
            }
        }
    }
}
