package com.example.foodapp.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate800
import com.example.foodapp.Slate900
import com.example.foodapp.data.model.CartItem as ApiCartItem
import com.example.foodapp.data.model.PickupOrderRequest
import com.example.foodapp.ui.components.BackHeader

@Composable
fun CheckoutScreen(state: AppState, viewModel: CheckoutViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackHeader("Checkout & Pagamento") { state.screen = "cart" } }

        errorMessage?.let { err ->
            item {
                Text(err, color = Brand, fontSize = 12.sp)
            }
        }

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
                        pickupType = "immediate",
                        scheduledTime = null,
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
                    Text("Conferma e Invia Ordine al Server", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
