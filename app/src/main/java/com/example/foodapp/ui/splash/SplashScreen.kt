package com.example.foodapp.ui.splash

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.BrandDark
import com.example.foodapp.EmeraldBg

@Composable
fun SplashScreen(state: AppState, onStart: () -> Unit = { state.screen = "home" }) {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Brand, BrandDark))
            )
            .padding(32.dp)
    ) {
        Column(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) { Text("🍔", fontSize = 40.sp) }
            Spacer(Modifier.height(12.dp))
            Text("FastBite", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Text(
                "Ordina in app. Salta la fila. Ritira caldo in cassa.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
        Column(Modifier.align(Alignment.BottomCenter)) {
            Card(
                colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(
                    1.dp,
                    Color.White.copy(alpha = 0.3f),
                    RoundedCornerShape(20.dp)
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Zero costi di consegna o commissioni rider",
                        "Sedi selezionate con indicazioni mappa",
                        "Pass QR code rapido per ritiro espresso"
                    ).forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint = EmeraldBg,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = it,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(Color.White, contentColor = Brand)
            ) { Text("Inizia ad Ordinare", fontWeight = FontWeight.ExtraBold) }
        }
    }
}
