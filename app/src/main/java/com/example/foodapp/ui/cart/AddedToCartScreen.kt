package com.example.foodapp.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.ui.Theme.FastBiteRed
import com.example.foodapp.ui.Theme.FastBiteTheme

@Composable
fun AddedToCartScreen(
    item: CartItem,
    onGoToCart: () -> Unit = {},
    onContinueShopping: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Icon(
            Icons.Default.CheckCircle,
            null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Aggiungi al carrello!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                if (item.selectedOptions.isNotEmpty()) {
                    Text(
                        "Opzioni : ${item.selectedOptions.joinToString { ", " }}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Quantità:", color = Color.Gray)
                    Text("${item.quantity}", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Prezzo Unitario:", color = Color.Gray)
                    Text("€${"%.2f".format(item.unitPrice)}", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Totale:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "€${"%.2f".format(item.totalPrice)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = FastBiteRed
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onGoToCart,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FastBiteRed)
        ) {
            Text(
                "Vai al Carrello",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onContinueShopping,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continua lo Shopping", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddedToCartPreview() {
    FastBiteTheme {
        AddedToCartScreen(
            item = CartItem("p1", "Big Mac", 5.99, 2, listOf("maxi", "bacon"), 1.50)
        )
    }
}