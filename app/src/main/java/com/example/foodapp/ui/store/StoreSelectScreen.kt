package com.example.foodapp.ui.store

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.foodapp.Emerald
import com.example.foodapp.EmeraldBg
import com.example.foodapp.Slate200
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.ui.components.BackHeader
import com.example.foodapp.ui.components.ErrorBanner

@Composable
fun StoreSelectScreen(state: AppState, viewModel: StoreViewModel = viewModel()) {
    val stores by viewModel.stores.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStores()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BackHeader("Seleziona Punto Vendita") {
            state.screen = state.previousScreen.ifBlank { "profile" }
        }

        errorMessage?.let { message ->
            ErrorBanner(message = message, onRetry = { viewModel.loadStores() })
        }

        if (isLoading) {
            Box(Modifier
                .fillMaxWidth()
                .padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(stores) { store ->
                    val sel = state.selectedStore.name == store.name
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(if (sel) Brand.copy(alpha = 0.06f) else Color.White),
                        border = BorderStroke(
                            if (sel) 2.dp else 1.dp, if (sel) Brand else Slate200
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                state.selectedStore = com.example.foodapp.Store(
                                    id = store.id.toIntOrNull() ?: 1,
                                    name = store.name,
                                    address = store.address,
                                    distance = store.distanceKm.toString() + " km",
                                    prepTime = "${store.readyInMinutes} min",
                                    isNearest = store.isPreferred
                                )
                                state.screen = state.previousScreen.ifBlank { "summary" }
                            }
                    ) {
                        Row(
                            Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(store.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    if (store.isPreferred) {
                                        Spacer(Modifier.width(6.dp))
                                        Surface(color = EmeraldBg, shape = RoundedCornerShape(50)) {
                                            Text(
                                                "Più vicino",
                                                Modifier.padding(
                                                    horizontal = 6.dp,
                                                    vertical = 1.dp
                                                ),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Emerald
                                            )
                                        }
                                    }
                                }
                                Text(store.address, fontSize = 11.sp, color = Slate500)
                                Text(
                                    "📍 ${store.distanceKm} km • Pronto in ${store.readyInMinutes} min",
                                    fontSize = 10.sp, color = Slate400
                                )
                            }
                            if (sel) Icon(Icons.Default.CheckCircle, null, tint = Brand)
                        }
                    }
                }
            }
        }
    }
}
