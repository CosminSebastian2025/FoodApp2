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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Emerald
import com.example.foodapp.EmeraldBg
import com.example.foodapp.OrderHistoryItem
import com.example.foodapp.Slate100
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate700
import com.example.foodapp.Slate900
import com.example.foodapp.ui.components.BackHeader

@Composable
fun OrderHistoryCard(order: OrderHistoryItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(order.id, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    Surface(color = EmeraldBg, shape = RoundedCornerShape(50)) {
                        Text(
                            order.status,
                            Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald
                        )
                    }
                }
                Text(order.date, fontSize = 10.sp, color = Slate400)
            }
            Text(
                "📍 ${order.storeName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate700
            )
            Text(
                order.itemsSummary,
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
    val order = state.activeOrderHistoryItem ?: return
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            BackHeader("Dettaglio Ordine ${order.id}") { state.screen = "profile" }
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
                        Text("NUMERO ORDINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)
                        Surface(color = EmeraldBg, shape = RoundedCornerShape(50)) {
                            Text(
                                order.status,
                                Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald
                            )
                        }
                    }
                    Text(order.id, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Data: ${order.date}", fontSize = 11.sp, color = Slate400)
                }
            }
        }

        // Tipo di ordine, Dove lo va a ritirare, Pin
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("INFORMAZIONI DI RITIRO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tipo di Ordine:", fontSize = 11.sp, color = Slate500)
                        Text(order.orderType, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    }

                    HorizontalDivider(color = Slate100)

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Dove lo va a ritirare:", fontSize = 11.sp, color = Slate500)
                        Spacer(Modifier.height(2.dp))
                        Text(order.storeName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        Text(order.storeAddress, fontSize = 11.sp, color = Slate500)
                    }

                    HorizontalDivider(color = Slate100)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("PIN Ritiro Cassa:", fontSize = 11.sp, color = Slate500)
                        Text(order.pin, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Brand)
                    }
                }
            }
        }

        // Tipo di pagamento fatto & Totale spesa
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("PAGAMENTO E SPESA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tipo di Pagamento:", fontSize = 11.sp, color = Slate500)
                        Text(order.paymentMethod, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate700)
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
                    Text("ARTICOLI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)
                    Text(order.itemsSummary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
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
