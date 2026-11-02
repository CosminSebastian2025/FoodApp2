package com.example.foodapp.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate800
import com.example.foodapp.Slate900
import com.example.foodapp.ui.components.BackHeader
import com.example.foodapp.ui.components.PayOption
import com.example.foodapp.ui.components.TimeChip

@Composable
fun CheckoutScreen(state: AppState) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackHeader("Checkout & Pagamento") { state.screen = "cart" } }
        // Locale + mappa
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(
                                "LOCALE DI RITIRO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate400
                            )
                            Text(
                                state.selectedStore.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(state.selectedStore.address, fontSize = 11.sp, color = Slate500)
                        }
                        Text(
                            "Cambia", color = Brand, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { state.screen = "store-select" })
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate800),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📍", fontSize = 24.sp)
                            Surface(color = Slate900, shape = RoundedCornerShape(50)) {
                                Text(
                                    state.selectedStore.name,
                                    Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
        // Orario
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "ORARIO DI RITIRO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TimeChip(
                            "Subito",
                            "~ ${state.selectedStore.prepTime}",
                            state.pickupTime == "ASAP"
                        ) {
                            state.pickupTime = "ASAP"
                        }
                        TimeChip(
                            "Programmato",
                            "12:45",
                            state.pickupTime == "12:45"
                        ) { state.pickupTime = "12:45" }
                        TimeChip(
                            "Programmato",
                            "13:15",
                            state.pickupTime == "13:15"
                        ) { state.pickupTime = "13:15" }
                    }
                }
            }
        }
        // Pagamento
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "METODO DI PAGAMENTO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    PayOption("card", "💳 Carta di Credito / Google Pay", state)
                    PayOption("paypal", "💸 PayPal", state)
                    PayOption("cash", "💵 Paga in cassa al ritiro", state)
                }
            }
        }
        item {
            Button(
                onClick = { state.screen = "summary" },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(Slate900)
            ) {
                Text("Vai al Riepilogo Ordine", fontWeight = FontWeight.Bold)
            }
        }
    }
}
