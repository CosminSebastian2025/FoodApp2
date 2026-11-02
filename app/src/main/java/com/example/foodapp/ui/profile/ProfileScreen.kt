package com.example.foodapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Slate100
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate700

@Composable
fun ProfileScreen(state: AppState) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Il Mio Profilo FastBite", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
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
                            if (state.isLoggedIn) "M" else "G", color = Color.White,
                            fontWeight = FontWeight.Black, fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (state.isLoggedIn) "Marco Rossi" else "Utente Ospite",
                            fontWeight = FontWeight.Bold, fontSize = 13.sp
                        )
                        Text(
                            if (state.isLoggedIn) "marco.rossi@email.it" else "Accedi per salvare i locali preferiti",
                            fontSize = 11.sp, color = Slate500
                        )
                    }
                    if (!state.isLoggedIn)
                        TextButton(onClick = { state.screen = "auth" }) {
                            Text(
                                "Accedi",
                                color = Brand
                            )
                        }
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { state.screen = "store-select" }
                            .padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Store,
                                null,
                                tint = Brand,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Punto Vendita Preferito",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(state.selectedStore.name, fontSize = 11.sp, color = Slate400)
                    }
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { state.screen = "payment-history" }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccountBox,
                                null,
                                tint = Brand,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Metodi di Pagamento Salvati",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Slate400)
                    }
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { state.screen = "payment-history" }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                null,
                                tint = Brand,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Storico Pagamenti",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Slate400)
                    }
                }
            }
        }

        // ---------- Sezione Storico Ordini ----------
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Storico Ordini", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                if (state.orderHistory.isNotEmpty()) {
                    Text(
                        "${state.orderHistory.size} ordini",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }
            }
        }

        if (state.orderHistory.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📋", fontSize = 32.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Nessun ordine nello storico",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Slate700
                        )
                        Text(
                            "I tuoi ordini completati appariranno qui.",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }
                }
            }
        } else {
            items(state.orderHistory) { order ->
                OrderHistoryCard(order) { state.openOrderDetail(order) }
            }
        }

        if (state.isLoggedIn) {
            item {
                OutlinedButton(
                    onClick = { state.isLoggedIn = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Disconnetti Profilo") }
            }
        }
    }
}
