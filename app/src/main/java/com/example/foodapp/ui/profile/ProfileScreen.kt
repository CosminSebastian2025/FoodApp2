package com.example.foodapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Slate100
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate700

@Composable
fun ProfileScreen(state: AppState, viewModel: ProfileViewModel = viewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    val orderHistoryApi by viewModel.orderHistory.collectAsState()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            try {
                viewModel.loadProfileData("user_123")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Il Mio Profilo FastBite", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        // Scheda Utente
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brand),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (state.isLoggedIn) (userProfile?.name?.take(1) ?: "M") else "G",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (state.isLoggedIn) (userProfile?.name ?: "Marco Rossi") else "Utente Ospite",
                            fontWeight = FontWeight.Bold, fontSize = 13.sp
                        )
                        Text(
                            if (state.isLoggedIn) (userProfile?.email ?: "marco.rossi@email.it") else "Accedi per ordinare in velocità",
                            fontSize = 11.sp, color = Slate500
                        )
                    }
                    if (!state.isLoggedIn) {
                        Button(
                            onClick = { state.screen = "auth" },
                            colors = ButtonDefaults.buttonColors(Brand),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Accedi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { state.isLoggedIn = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Esci", fontSize = 11.sp, color = Slate700)
                        }
                    }
                }
            }
        }

        // Punto vendita preferito
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Color.White),
                modifier = Modifier.clickable { state.screen = "store-select" }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Store, null, tint = Brand, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Punto Vendita Preferito", fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Bold)
                            Text(state.selectedStore.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Slate400)
                }
            }
        }

        // Storico pagamenti
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Color.White),
                modifier = Modifier.clickable { state.screen = "payment-history" }
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CreditCard, null, tint = Brand, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Storico Pagamenti", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Slate400)
                }
            }
        }

        // Storico ordini
        item {
            Text("I Tuoi Ordini Recenti", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        if (state.orderHistory.isEmpty() && orderHistoryApi.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Nessun ordine effettuato", fontSize = 12.sp, color = Slate500)
                    }
                }
            }
        } else {
            items(state.orderHistory) { order ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    modifier = Modifier.clickable { state.openOrderDetail(order) }
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ordine ${order.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("€${"%.2f".format(order.total)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Brand)
                        }
                        Text(order.itemsSummary, fontSize = 11.sp, color = Slate500)
                        Text("${order.date} • ${order.status}", fontSize = 10.sp, color = Slate400)
                    }
                }
            }
        }
    }
}
