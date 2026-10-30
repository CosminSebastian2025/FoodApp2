package com.example.foodapp.ui.checkout

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.data.model.ImmediateSlot
import com.example.foodapp.data.model.PaymentMethod
import com.example.foodapp.data.model.PickupSlots
import com.example.foodapp.data.model.ScheduledSlot
import com.example.foodapp.data.model.Store
import com.example.foodapp.ui.Theme.FastBiteGray
import com.example.foodapp.ui.Theme.FastBiteRed
import com.example.foodapp.ui.Theme.FastBiteTextGray
import com.example.foodapp.ui.Theme.FastBiteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    total: Double = 13.98,
    selectedStore: Store? = null,
    selectedSlot: String? = null,
    selectedPayment: PaymentMethod? = null,
    onStoreChange: () -> Unit = {},
    onSelectSlot: (String) -> Unit = {},
    onSelectPayment: (String) -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    var store by remember { mutableStateOf(selectedStore) }
    var slot by remember { mutableStateOf(selectedSlot ?: "immediate") }
    var payment by remember { mutableStateOf(selectedPayment?.id ?: "pay_visa") }

    val stores = listOf(
        Store("s1", "FastBite Milano Centro", "Via Dante 12", 1.2, 15, "10:00-23:00", true),
        Store("s2", "FastBite Porta Romana", "Corso di Porta Romana 45", 2.8, 20, "10:00-23:30", false)
    )
    val slots = PickupSlots(
        ImmediateSlot("Subito", "15 min"),
        listOf(
            ScheduledSlot("slot_1", "12:45", true),
            ScheduledSlot("slot_2", "13:15", true),
            ScheduledSlot("slot_3", "13:45", false)
        )
    )
    val payments = listOf(
        PaymentMethod("pay_visa", "CARD", "Visa •••• 4242", true),
        PaymentMethod("pay_apple", "APPLE_PAY", "Apple Pay", false),
        PaymentMethod("pay_paypal", "PAYPAL", "PayPal", false),
        PaymentMethod("pay_cash", "CASH", "Paga in cassa al ritiro", false)
    )

    Scaffold(topBar = {
        TopAppBar(title = { Text("Riepilogo Ritiro in Cassa", fontWeight = FontWeight.Bold) })
    }) { padding ->
        Column(Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())) {
            // LOCALE
            SectionTitle("Locale di Ritiro")
            Card(Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, null, tint = FastBiteRed, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(store?.name ?: stores[0].name, fontWeight = FontWeight.SemiBold)
                        Text(store?.address ?: stores[0].address,
                            fontSize = 13.sp, color = FastBiteTextGray)
                    }
                    TextButton(onClick = onStoreChange) { Text("Cambia") }
                }
            }

            // ORARIO
            SectionTitle("Orario di Ritiro")
            Column(Modifier.padding(horizontal = 16.dp)) {
                SlotChip(
                    slots.immediated.label, slot == "immediate",
                    onClick = { slot = "immediate" })
                slots.scheduled.filter { it.available }.forEach { s ->
                    SlotChip("Programmato ${s.time}", slot == s.slotId,
                        onClick = { slot = s.slotId })
                }
            }

            // PAGAMENTO
            SectionTitle("Seleziona Pagamento")
            Column(Modifier.padding(horizontal = 16.dp)) {
                payments.forEach { pm ->
                    PaymentRow(pm, payment == pm.id) { payment = pm.id }
                }
            }

            // TOTALE
            Card(Modifier
                .fillMaxWidth()
                .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = FastBiteGray)) {
                Row(Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Totale da pagare", fontWeight = FontWeight.Bold)
                    Text("€${"%.2f".format(total)}", fontWeight = FontWeight.Bold,
                        color = FastBiteRed, fontSize = 20.sp)
                }
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FastBiteRed)
            ) {
                Text("Invia Ordine Asporto", color = Color.White,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp))
}

@Composable
private fun SlotChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = Modifier.padding(vertical = 4.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FastBiteRed.copy(alpha = 0.1f),
            selectedLabelColor = FastBiteRed
        )
    )
}

@Composable
private fun PaymentRow(pm: PaymentMethod, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) FastBiteRed.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, if (selected) FastBiteRed else Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = FastBiteRed))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(pm.label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                if (pm.isDefault) Text("Predefinito", fontSize = 11.sp, color = FastBiteTextGray)
            }
            when (pm.type) {
                "CARD" -> Icon(Icons.Default.CreditCard, null, tint = FastBiteTextGray)
                "APPLE_PAY" -> Text("🍎", fontSize = 20.sp)
                "PAYPAL" -> Text("🅿️", fontSize = 20.sp)
                "CASH" -> Icon(Icons.Default.Payments, null, tint = FastBiteTextGray)
            }
        }
    }
}

// ===== PREVIEW =====
@Preview(showBackground = true, showSystemUi = true, name = "Checkout - Completo")
@Composable
private fun CheckoutPreview() {
    FastBiteTheme { CheckoutScreen() }
}