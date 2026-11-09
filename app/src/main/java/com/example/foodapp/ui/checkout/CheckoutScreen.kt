package com.example.foodapp.ui.checkout

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.foodapp.MANAGEMENT_FEE
import com.example.foodapp.Slate100
import com.example.foodapp.Slate200
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate800
import com.example.foodapp.Slate900
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.PickupOrderRequest
import com.example.foodapp.ui.cart.CartViewModel
import com.example.foodapp.ui.components.BackHeader
import com.example.foodapp.ui.components.ErrorBanner
import com.example.foodapp.ui.components.TotalsCard

@Composable
fun CheckoutScreen(
    state: AppState,
    viewModel: CheckoutViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val pickupSlots by cartViewModel.pickupSlots.collectAsState()

    LaunchedEffect(Unit) {
        if (cartViewModel.pickupSlots.value == null) cartViewModel.loadCartData()
    }

    val timeOptions = remember(pickupSlots) {
        buildList {
            add("ASAP")
            pickupSlots?.scheduled?.filter { it.available }?.forEach { add(it.time) }
        }
    }

    LaunchedEffect(timeOptions) {
        if (state.pickupTime !in timeOptions) state.pickupTime = "ASAP"
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackHeader("Checkout & Pagamento") { state.screen = "summary" } }

        errorMessage?.let { err ->
            item {
                ErrorBanner(message = err)
            }
        }

        state.checkoutError?.let { msg ->
            item {
                ErrorBanner(message = msg)
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
                            border = if (sel) BorderStroke(2.dp, Brand) else BorderStroke(
                                1.dp,
                                Slate200
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { state.paymentMethod = id }
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
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
                    if (isLoading) return@Button
                    state.checkoutError = null

                    val currentUserId = state.userId
                    if (currentUserId.isNullOrBlank()) {
                        state.checkoutError = "Accedi per completare l'ordine."
                        state.authReturnScreen = "checkout"
                        state.screen = "auth"
                        return@Button
                    }

                    if (state.selectedStore.id.isBlank()) {
                        state.checkoutError = "Seleziona un punto vendita prima di confermare l'ordine."
                        state.previousScreen = "checkout"
                        state.screen = "store-select"
                        return@Button
                    }

                    val apiItems = state.cart.map { ci ->
                        CartItem(
                            productId = ci.product.id,
                            productName = ci.product.name,
                            basePrice = ci.product.price,
                            quantity = ci.quantity,
                            selectedOptions = listOf(ci.size.name) + ci.extras.map { it.name },
                            optionPriceModifier = ci.size.price + ci.extras.sumOf { it.price }
                        )
                    }

                    val request = PickupOrderRequest(
                        userId = currentUserId,
                        storeId = state.selectedStore.id,
                        pickupType = if (state.pickupTime == "ASAP") "immediate" else "scheduled",
                        scheduledTime = if (state.pickupTime != "ASAP") state.pickupTime else null,
                        discountCode = if (state.promoApplied) state.promoInput else null,
                        items = apiItems,
                        subTotal = state.cartSubtotal,
                        discount = state.discountAmount,
                        managementFee = MANAGEMENT_FEE,
                        total = state.cartFinalTotal,
                        paymentMethod = state.paymentMethod.uppercase()
                    )
                    viewModel.submitOrder(request) { conf ->
                        state.orderId = conf.orderId.orEmpty()
                        state.pickupPin = conf.pickupPin.orEmpty()
                        state.cart.clear()
                        state.screen = "confirmation"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(Brand)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Conferma Ordine (€${"%.2f".format(state.cartFinalTotal)})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


