package com.example.foodapp.ui.cart

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Emerald
import com.example.foodapp.EmeraldBg
import com.example.foodapp.Slate100
import com.example.foodapp.Slate400
import com.example.foodapp.Slate900
import com.example.foodapp.ui.components.SmallQtyButton
import com.example.foodapp.ui.components.TotalsCard

@Composable
fun CartScreen(state: AppState) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Il Tuo Carrello", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Surface(color = EmeraldBg, shape = RoundedCornerShape(50)) {
                    Text(
                        "Ritiro Gratuito", Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Emerald
                    )
                }
            }
        }
        if (state.cart.isEmpty()) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🛍", fontSize = 48.sp)
                    Text("Il tuo carrello di asporto è vuoto", color = Slate400, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { state.screen = "categories" },
                        colors = ButtonDefaults.buttonColors(Brand)
                    ) { Text("Sfoglia Menu") }
                }
            }
        }
        items(state.cart.size) { i ->
            val item = state.cart[i]
            Card(
                shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Slate100),
                                contentAlignment = Alignment.Center
                            ) { Text(item.product.emoji, fontSize = 22.sp) }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    item.product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    item.size.name + if (item.extras.isNotEmpty()) " • " +
                                            item.extras.joinToString(", ") { it.name } else "",
                                    fontSize = 10.sp,
                                    color = Slate400,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Text(
                            "€${"%.2f".format(item.itemTotal * item.quantity)}",
                            fontWeight = FontWeight.ExtraBold, fontSize = 12.sp
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { state.cart.removeAt(i) }) {
                            Icon(
                                Icons.Default.Delete,
                                null,
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SmallQtyButton("-") {
                                val q = item.quantity - 1
                                if (q <= 0) state.cart.removeAt(i)
                                else state.cart[i] = item.copy(quantity = q)
                            }
                            Text(
                                "${item.quantity}",
                                Modifier.width(28.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            SmallQtyButton("+") {
                                state.cart[i] = item.copy(quantity = item.quantity + 1)
                            }
                        }
                    }
                }
            }
        }
        if (state.cart.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "CODICE SCONTO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400
                        )
                        Row {
                            OutlinedTextField(
                                value = state.promoInput,
                                onValueChange = { state.promoInput = it },
                                placeholder = { Text("Inserisci es. FAST10", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand)
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { state.applyPromo() },
                                colors = ButtonDefaults.buttonColors(Slate900),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Applica", fontSize = 11.sp) }
                        }
                        if (state.promoApplied)
                            Text(
                                "Sconto 10% applicato all'asporto!", fontSize = 10.sp,
                                fontWeight = FontWeight.Bold, color = Emerald
                            )
                    }
                }
            }
            item { TotalsCard(state) }
            item {
                Button(
                    onClick = { state.proceedToCheckout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(Brand)
                ) {
                    Row(
                        Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Procedi al Checkout", fontWeight = FontWeight.Bold)
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
