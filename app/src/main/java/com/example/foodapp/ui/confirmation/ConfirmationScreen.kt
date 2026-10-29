package com.fastbite.app.ui.confirmation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fastbite.app.ui.theme.FastBiteRed
import com.fastbite.app.ui.theme.FastBiteTheme

@Composable
fun ConfirmationScreen(
    orderId: String = "ORD-1234567890",
    pickupPin: String = "4821",
    pickupTime: String = "Subito (~15 min)",
    storeName: String = "FastBite Milano Centro",
    total: Double = 13.98,
    onBackHome: () -> Unit = {}
) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Icon(Icons.Default.CheckCircle, null, tint = FastBiteRed, modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(16.dp))
        Text("Ordine Inviato con Successo!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("Mostra il pass al banco quando l'ordine è pronto",
            color = Color.Gray, textAlign = TextAlign.Center)

        Spacer(Modifier.height(24.dp))

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Codice Ordine Ritiro", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(orderId, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(Modifier.height(20.dp))

                Text("PIN di Chiamata Cassa", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(pickupPin,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold, color = FastBiteRed,
                    letterSpacing = 8.sp)

                Spacer(Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                InfoRow("Orario Ritiro:", pickupTime)
                Spacer(Modifier.height(8.dp))
                InfoRow("Locale:", storeName)
                Spacer(Modifier.height(8.dp))
                InfoRow("Totale:", "€${"%.2f".format(total)}")

                Spacer(Modifier.height(16.dp))
                // QR Code placeholder
                Surface(
                    Modifier.size(150.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEEEEEE)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("QR", fontSize = 48.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FastBiteRed)
        ) {
            Text("Torna al Menu Principale", color = Color.White,
                fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Confirmation - Ordine confermato")
@Composable
private fun ConfirmationPreview() {
    FastBiteTheme { ConfirmationScreen() }
}