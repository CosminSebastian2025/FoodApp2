package com.example.foodapp.ui.summary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.foodapp.Slate100
import com.example.foodapp.Slate400
import com.example.foodapp.Slate700
import com.example.foodapp.data.model.PickupOrderRequest
import com.example.foodapp.ui.checkout.CheckoutViewModel
import com.example.foodapp.ui.components.BackHeader
import com.example.foodapp.ui.components.InfoRow
import com.example.foodapp.ui.components.SectionHeader
import com.example.foodapp.ui.components.TotalsCard
import com.example.foodapp.data.model.CartItem as ApiCartItem

@Composable
fun SummaryScreen(state: AppState, viewModel: CheckoutViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val confirmation by viewModel.orderConfirmation.collectAsState()

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BackHeader("Verifica & Riepilogo Finale") { state.screen = "cart" }
            Text(
                "Controlla i dati, modifica il pagamento o il punto ritiro",
                fontSize = 10.sp, color = Slate400
            )
        }

        errorMessage?.let { err ->
            item {
                Text(err, color = Brand, fontSize = 12.sp)
            }
        }

        // Articoli Selezionati
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader("Articoli Selezionati", "Modifica Carrello") {
                        state.screen = "cart"
                    }
                    state.cart.forEach { item ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = Slate100, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        "${item.quantity}x",
                                        Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        item.product.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(item.size.name, fontSize = 9.sp, color = Slate400)
                                }
                            }
                            Text(
                                "€${"%.2f".format(item.itemTotal * item.quantity)}",
                                fontSize = 12.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
                    // Aggiunta last-minute
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Brand.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, Brand.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                state.previousScreen = "summary"
                                state.screen = "add-more"
                            }
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                null,
                                tint = Brand,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "+ Aggiungi altri articoli al tuo ordine",
                                fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Brand
                            )
                        }
                    }
                }
            }
        }

        // Info Ritiro (Store + Orario)
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionHeader("Info Ritiro", "Cambia") {
                        state.previousScreen = "summary"
                        state.screen = "store-select"
                    }
                    InfoRow("Punto Vendita:", state.selectedStore.name)
                    InfoRow("Indirizzo:", state.selectedStore.address)
                    InfoRow(
                        "Orario Previsto:",
                        if (state.pickupTime == "ASAP") "Subito (${state.selectedStore.prepTime})" else state.pickupTime,
                        valueColor = Brand
                    )
                }
            }
        }

        // Metodo di Pagamento con tasto Modifica che rimanda a payment-select
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionHeader("Metodo di Pagamento", "Cambia") {
                        state.previousScreen = "summary"
                        state.screen = "payment-select"
                    }
                    val label = when (state.paymentMethod) {
                        "card" -> "💳 Carta di Credito / Google Pay"
                        "paypal" -> "💸 PayPal"
                        else -> "💵 Contanti al ritiro in cassa"
                    }
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                }
            }
        }

        item { TotalsCard(state) }

        item {
            Button(
                onClick = {
                    val apiItems = state.cart.map { ci ->
                        ApiCartItem(
                            productId = ci.product.id,
                            productName = ci.product.name,
                            basePrice = ci.product.price,
                            quantity = ci.quantity,
                            selectedOptions = listOf(ci.size.name) + ci.extras.map { it.name },
                            optionPriceModifier = ci.size.price + ci.extras.sumOf { it.price }
                        )
                    }

                    val currentUserId = state.userId
                    if (currentUserId.isNullOrBlank()) {
                        return@Button
                    }

                    val request = PickupOrderRequest(
                        userId = currentUserId,
                        storeId = state.selectedStore.id.toString(),
                        pickupType = if (state.pickupTime == "ASAP") "immediate" else "scheduled",
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
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(
                        Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Conferma ed Effettua Ordine", fontWeight = FontWeight.Bold)
                        Text(
                            "€${"%.2f".format(state.cartFinalTotal)}",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
