package com.example.foodapp.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Slate200
import com.example.foodapp.Slate800
import com.example.foodapp.ui.components.BackHeader

@Composable
fun PaymentMethodSelectScreen(state: AppState) {
    val methods = listOf(
        "card" to "💳 Carta di Credito / Google Pay / Apple Pay",
        "paypal" to "💸 PayPal",
        "cash" to "💵 Contanti al Ritiro in Cassa"
    )

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BackHeader("Seleziona Metodo di Pagamento") {
                state.screen = state.previousScreen.ifBlank { "summary" }
            }
        }
        items(methods.size) { i ->
            val (id, label) = methods[i]
            val sel = state.paymentMethod == id
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (sel) Brand.copy(alpha = 0.08f) else Color.White,
                border = if (sel) BorderStroke(2.dp, Brand) else BorderStroke(1.dp, Slate200),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        state.paymentMethod = id
                        state.screen = state.previousScreen.ifBlank { "summary" }
                    }
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
                    RadioButton(
                        selected = sel,
                        onClick = {
                            state.paymentMethod = id
                            state.screen = state.previousScreen.ifBlank { "summary" }
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = Brand)
                    )
                }
            }
        }
    }
}
