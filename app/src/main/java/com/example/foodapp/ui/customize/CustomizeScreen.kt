package com.example.foodapp.ui.customize

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Slate100
import com.example.foodapp.Slate200
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate700
import com.example.foodapp.extraOptions
import com.example.foodapp.sizeOptions
import com.example.foodapp.ui.components.BackHeader
import com.example.foodapp.ui.components.SmallQtyButton

@Composable
fun CustomizeScreen(state: AppState) {
    val p = state.activeProduct ?: return
    val extrasSum = state.selectedExtras.sumOf { it.price }
    val unitPrice = p.price + state.selectedSize.price + extrasSum

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { BackHeader("Personalizza Articolo") { state.goBackFromCustomize() } }
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Slate100),
                contentAlignment = Alignment.Center
            ) { Text(p.emoji, fontSize = 72.sp) }
        }
        item {
            Column {
                Text(p.name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(p.description, fontSize = 12.sp, color = Slate500)
            }
        }
        // Dimensione
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "DIMENSIONE PORZIONE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sizeOptions.forEach { size ->
                            val sel = state.selectedSize.id == size.id
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (sel) Brand else Slate100,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { state.selectedSize = size }
                            ) {
                                Row(
                                    Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        size.name, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        color = if (sel) Color.White else Slate700
                                    )
                                    Text(
                                        if (size.price > 0) "+€${"%.2f".format(size.price)}" else "Incluso",
                                        fontSize = 11.sp,
                                        color = if (sel) Color.White.copy(alpha = 0.8f) else Slate500
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // Extra
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "AGGIUNGI EXTRA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    extraOptions.forEach { extra ->
                        val checked = state.selectedExtras.any { it.id == extra.id }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate100)
                                .clickable {
                                    if (checked) state.selectedExtras.removeIf { it.id == extra.id }
                                    else state.selectedExtras.add(extra)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = checked, onCheckedChange = {
                                        if (it) state.selectedExtras.add(extra)
                                        else state.selectedExtras.removeIf { e -> e.id == extra.id }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = Brand)
                                )
                                Text(extra.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text(
                                "+€${"%.2f".format(extra.price)}", fontSize = 12.sp,
                                fontWeight = FontWeight.Bold, color = Slate500
                            )
                        }
                    }
                }
            }
        }
        // Quantità + conferma
        item {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, Slate200, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    SmallQtyButton("-") { state.customQty = maxOf(1, state.customQty - 1) }
                    Text(
                        "${state.customQty}", Modifier.width(28.dp), textAlign = TextAlign.Center,
                        fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                    SmallQtyButton("+") { state.customQty++ }
                }
                Button(
                    onClick = { state.confirmAddToCart() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(Brand),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        "Conferma • €${"%.2f".format(unitPrice * state.customQty)}",
                        fontWeight = FontWeight.Bold, fontSize = 12.sp
                    )
                }
            }
        }
    }
}
