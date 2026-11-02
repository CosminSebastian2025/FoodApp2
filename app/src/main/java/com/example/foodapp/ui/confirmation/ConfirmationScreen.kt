package com.example.foodapp.ui.confirmation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.foodapp.Emerald
import com.example.foodapp.EmeraldBg
import com.example.foodapp.Slate100
import com.example.foodapp.Slate200
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate800
import com.example.foodapp.Slate900

@Composable
fun ConfirmationScreen(state: AppState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(EmeraldBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, null, tint = Emerald, modifier = Modifier.size(28.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ordine Inviato con Successo!", fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(
                "Mostra il pass al banco quando l'ordine è pronto",
                fontSize = 11.sp, color = Slate500
            )
        }
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(Color.White),
            border = BorderStroke(2.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "CODICE ORDINE RITIRO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )
                Text(state.orderId, fontSize = 26.sp, fontWeight = FontWeight.Black)
                Box(
                    Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Slate100),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "■□■□■",
                        fontSize = 22.sp,
                        color = Slate800,
                        lineHeight = 8.sp
                    )
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
                Text(
                    "PIN DI CHIAMATA CASSA",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )
                Text(
                    state.pickupPin,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Brand
                )
            }
        }
        Card(
            shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Slate100),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "📍 ${state.selectedStore.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(state.selectedStore.address, fontSize = 11.sp, color = Slate500)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Orario Ritiro:", fontSize = 11.sp, color = Slate500)
                    Text(
                        if (state.pickupTime == "ASAP") state.selectedStore.prepTime else state.pickupTime,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Button(
            onClick = { state.resetApp() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(Slate900)
        ) { Text("Torna al Menu Principale") }
    }
}
