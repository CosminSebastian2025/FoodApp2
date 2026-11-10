package com.example.foodapp.ui.addmore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Slate100
import com.example.foodapp.Slate400
import com.example.foodapp.Slate900
import com.example.foodapp.ui.categories.CategoriesViewModel
import com.example.foodapp.ui.components.BackHeader
import com.example.foodapp.ui.components.ProductImage

@Composable
fun AddMoreScreen(state: AppState, viewModel: CategoriesViewModel = viewModel()) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    // Un prodotto può stare in più categorie: controlliamo l'elenco completo, accettando anche gli ID legacy.
    val filtered = when (state.activeQuickCat) {
        "drinks" -> products.filter { product ->
            product.allCategories.any {
                it.equals("Bevande", true) || it.equals("drinks", true)
            }
        }
        "desserts" -> products.filter { product ->
            product.allCategories.any {
                it.equals("Dolci", true) || it.equals("desserts", true)
            }
        }
        else -> products
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackHeader("Aggiungi al tuo Ordine") { state.screen = "summary" } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "all" to "Tutti",
                    "drinks" to "Bibite",
                    "desserts" to "Dolci"
                ).forEach { (id, label) ->
                    FilterChip(
                        selected = state.activeQuickCat == id,
                        onClick = { state.activeQuickCat = id },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand, selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
        if (isLoading) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(32.dp), contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        } else {
            items(filtered) { p ->
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate100)
                        ) {
                            ProductImage(
                                raw = p.imageUrl,
                                contentDescription = p.name,
                                emojiFontSize = 26.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(p.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                p.description, fontSize = 10.sp, color = Slate400, maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "€${"%.2f".format(p.price)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Button(
                            onClick = { state.openCustomize(p) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(Brand),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("Seleziona", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
        item {
            Button(
                onClick = { state.screen = "summary" },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(Slate900)
            ) { Text("Torna al Riepilogo") }
        }
    }
}
