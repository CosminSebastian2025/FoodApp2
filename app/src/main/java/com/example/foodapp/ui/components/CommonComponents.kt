package com.example.foodapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.foodapp.MANAGEMENT_FEE
import com.example.foodapp.Slate100
import com.example.foodapp.Slate200
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate700
import com.example.foodapp.data.model.Product

@Composable
fun BackHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Slate700)
        }
        Spacer(Modifier.width(4.dp))
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

@Composable
fun RowScope.TimeChip(label: String, value: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (selected) Brand else Slate100,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (selected) Brand else Slate200
        ),
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
    ) {
        Column(
            Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else Slate400,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(Modifier.height(3.dp))
            Text(
                value,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (selected) Color.White else Slate700,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PayOption(value: String, label: String, state: AppState) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (state.paymentMethod == value) Brand.copy(alpha = 0.08f) else Slate100)
            .clickable { state.paymentMethod = value }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = state.paymentMethod == value, onClick = { state.paymentMethod = value },
            colors = RadioButtonDefaults.colors(selectedColor = Brand)
        )
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
    }
}

@Composable
fun TotalsCard(state: AppState) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            TotalsRow("Subtotale articoli", "€${"%.2f".format(state.cartSubtotal)}")
            if (state.promoApplied) TotalsRow(
                "Sconto applicato (-10%)",
                "-€${"%.2f".format(state.discountAmount)}",
                Emerald
            )
            TotalsRow("Costo gestione asporto", "€${"%.2f".format(MANAGEMENT_FEE)}")
            TotalsRow("Ritiro in cassa", "Gratuito", Emerald)
            HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Totale Finale", fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text(
                    "€${"%.2f".format(state.cartFinalTotal)}", fontWeight = FontWeight.Black,
                    fontSize = 14.sp, color = Brand
                )
            }
        }
    }
}

@Composable
fun TotalsRow(label: String, value: String, color: Color = Slate700) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = Slate500)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun SmallQtyButton(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Slate100)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700) }
}

@Composable
fun ProductCard(p: Product?, onCustomize: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(12.dp)) {
            Box(
                Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Slate100)
            ) {
                ProductImage(
                    raw = p?.imageUrl.orEmpty(),
                    contentDescription = p?.nameProduct,
                    emojiFontSize = 32.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    p?.nameProduct?.let {
                        Text(
                            p.nameProduct,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    p?.basePrice?.let {
                        Text(
                            "€${"%.2f".format(p.basePrice)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
                p?.description?.let {
                    Text(
                        p.description, fontSize = 11.sp, color = Slate500, maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onCustomize,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(Brand.copy(alpha = 0.12f), Brand),
                        elevation = null,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) { Text("Personalizza", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)
        Text(
            action, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Brand,
            modifier = Modifier.clickable(onClick = onAction)
        )
    }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = Slate700) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 11.sp, color = Slate500)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
