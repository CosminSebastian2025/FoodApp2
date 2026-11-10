package com.example.foodapp.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Emerald
import com.example.foodapp.EmeraldBg
import com.example.foodapp.Slate100
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate700
import com.example.foodapp.Slate900
import com.example.foodapp.data.model.OrderHistoryItemResponse
import com.example.foodapp.ui.components.BackHeader
import kotlinx.coroutines.delay

@Composable
fun OrderHistoryCard(order: OrderHistoryItemResponse, onClick: () -> Unit) {
    val status = order.status?.takeIf { it.isNotBlank() } ?: "In Preparazione"
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(order.id.orEmpty(), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    Surface(color = EmeraldBg, shape = RoundedCornerShape(50)) {
                        Text(
                            status,
                            Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald
                        )
                    }
                }
                Text(order.date.orEmpty(), fontSize = 10.sp, color = Slate400)
            }
            Text(
                "📍 ${order.storeName.orEmpty().ifBlank { "FastBite" }}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate700
            )
            Text(
                order.itemsSummary.orEmpty().ifBlank { "Ordine FastBite" },
                fontSize = 11.sp,
                color = Slate500,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Totale Ordine", fontSize = 11.sp, color = Slate400)
                Text(
                    "€${"%.2f".format(order.total)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = Brand
                )
            }
        }
    }
}

@Composable
fun OrderDetailScreen(state: AppState) {
    val viewModel: ProfileViewModel = viewModel()
    val orderHistory by viewModel.orderHistory.collectAsState()
    val snapshot = state.activeOrderHistoryItem
    if (snapshot == null) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            BackHeader("Dettaglio Ordine") { state.screen = "profile" }
            Spacer(Modifier.height(24.dp))
            Text(
                "Dettaglio non disponibile. Torna al profilo e riapri l'ordine.",
                fontSize = 12.sp,
                color = Slate500
            )
        }
        return
    }

    // Usa la versione più recente dello stesso ordine, così lo stato
    // aggiornato dal pannello admin appare anche qui.
    val order = orderHistory.firstOrNull { it.id == snapshot.id } ?: snapshot

    LaunchedEffect(snapshot.id, state.userId) {
        while (true) {
            viewModel.refreshOrderHistory(state.userId)
            delay(10_000)
        }
    }

    val status = order.status?.takeIf { it.isNotBlank() } ?: "In Preparazione"
    val orderId = order.id.orEmpty().ifBlank { "-" }
    val orderDate = order.date.orEmpty()
    val orderType = order.orderType.orEmpty().ifBlank { "Asporto - Ritiro in Cassa" }
    val storeName = order.storeName.orEmpty().ifBlank { "FastBite Store" }
    val storeAddress = order.storeAddress.orEmpty()
    val pin = order.pin.orEmpty().ifBlank { "-" }
    val paymentMethod = order.paymentMethod.orEmpty().ifBlank { "-" }
    val itemsSummary = order.itemsSummary.orEmpty().ifBlank { "Ordine FastBite" }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            BackHeader("Dettaglio Ordine") { state.screen = "profile" }
        }

        // Card Numero Ordine & Stato
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Slate900),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "NUMERO ORDINE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400
                        )
                        Surface(color = EmeraldBg, shape = RoundedCornerShape(50)) {
                            Text(
                                status,
                                Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald
                            )
                        }
                    }
                    Text(
                        orderId,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text("Data: $orderDate", fontSize = 11.sp, color = Slate400)
                }
            }
        }

        // Tipo di ordine, Dove lo va a ritirare, Pin
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "INFORMAZIONI DI RITIRO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tipo di Ordine:", fontSize = 11.sp, color = Slate500)
                        Text(
                            orderType,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                    }

                    HorizontalDivider(color = Slate100)

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Dove lo va a ritirare:", fontSize = 11.sp, color = Slate500)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            storeName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(storeAddress, fontSize = 11.sp, color = Slate500)
                    }

                    HorizontalDivider(color = Slate100)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("PIN Ritiro Cassa:", fontSize = 11.sp, color = Slate500)
                        Text(
                            pin,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Brand
                        )
                    }
                }
            }
        }

        // Tipo di pagamento fatto & Totale spesa
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "PAGAMENTO E SPESA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tipo di Pagamento:", fontSize = 11.sp, color = Slate500)
                        Text(
                            paymentMethod,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                    }

                    HorizontalDivider(color = Slate100)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Totale Spesa:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "€${"%.2f".format(order.total)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Brand
                        )
                    }
                }
            }
        }

        // Articoli ordinati
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "ARTICOLI",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    Text(
                        itemsSummary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                }
            }
        }

        item {
            Button(
                onClick = { state.screen = "profile" },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(Slate900)
            ) {
                Text("Torna al Profilo", fontWeight = FontWeight.Bold)
            }
        }
    }
}
