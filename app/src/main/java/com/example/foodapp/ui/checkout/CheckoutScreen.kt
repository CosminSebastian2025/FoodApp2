package com.example.foodapp.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.foodapp.Slate200
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate800
import com.example.foodapp.Slate900
import com.example.foodapp.data.model.CartItem as ApiCartItem
import com.example.foodapp.data.model.PickupOrderRequest
import com.example.foodapp.ui.components.BackHeader
import com.example.foodapp.ui.components.TotalsCard

@Composable
fun CheckoutScreen(state: AppState, viewModel: CheckoutViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val timeOptions = listOf("ASAP", "12:30", "13:00", "13:30", "19:30", "20:00")

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackHeader("Checkout & Pagamento") { state.screen = "summary" } }

        errorMessage?.let { err ->
            item {
                Text(err, color = Brand, fontSize = 12.sp)
            }
        }

        // 1. Locale di Ritiro + Mappa
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
                            modifier = Modifier.clickable { state.screen = "store-select" }
                        )
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate800),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📍", fontSize = 22.sp)
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

        // 2. Orario di Ritiro
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "ORARIO DI RITIRO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(timeOptions) { time ->
                            val sel = state.pickupTime == time
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (sel) Brand else Slate100,
                                modifier = Modifier.clickable { state.pickupTime = time }
                            ) {
                                Text(
                                    if (time == "ASAP") "Subito (~15m)" else time,
                                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sel) Color.White else Slate800
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Metodi di Pagamento
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "METODO DI PAGAMENTO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    listOf(
                        "card" to "💳 Carta di Credito / Google Pay",
                        "paypal" to "💸 PayPal",
                        "cash" to "💵 Contanti al Ritiro in Cassa"
                    ).forEach { (id, label) ->
                        val sel = state.paymentMethod == id
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (sel) Brand.copy(alpha = 0.06f) else Slate100,
                            border = if (sel) BorderStroke(2.dp, Brand) else BorderStroke(1.dp, Slate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { state.paymentMethod = id }
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                RadioButton(
                                    selected = sel,
                                    onClick = { state.paymentMethod = id },
                                    colors = RadioButtonDefaults.colors(selectedColor = Brand)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Totali e Costi
        item { TotalsCard(state) }

        // 5. Pulsante Conferma Ordine
        item {
            Button(
                onClick = {
                    val apiItems = state.cart.map { ci ->
                        ApiCartItem(
                            productId = ci.product.id.toString(),
                            productName = ci.product.name,
                            basePrice = ci.product.price,
                            quantity = ci.quantity,
                            selectedOptions = listOf(ci.size.name) + ci.extras.map { it.name },
                            optionPriceModifier = ci.size.price + ci.extras.sumOf { it.price }
                        )
                    }
                    val request = PickupOrderRequest(
                        userId = "user_123",
                        storeId = state.selectedStore.id.toString(),
                        pickupType = state.pickupTime,
                        scheduledTime = if (state.pickupTime != "ASAP") state.pickupTime else null,
                        discountCode = if (state.promoApplied) state.promoInput else null,
                        items = apiItems,
                        subTotal = state.cartSubtotal,
                        discount = state.discountAmount,
                        managementFee = 1.50,
                        total = state.cartFinalTotal
                    )
                    viewModel.submitOrder(request) { confirmation ->
                        state.orderId = confirmation.orderId
                        state.pickupPin = confirmation.pickupPin
                        state.cart.clear()
                        state.screen = "confirmation"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(Brand)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Conferma e Invia Ordine (€${"%.2f".format(state.cartFinalTotal)})", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
