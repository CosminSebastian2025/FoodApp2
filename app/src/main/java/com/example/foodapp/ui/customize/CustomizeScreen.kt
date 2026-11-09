package com.example.foodapp.ui.customize

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.foodapp.ui.components.ErrorBanner
import com.example.foodapp.ui.components.SmallQtyButton

@Composable
fun CustomizeScreen(state: AppState, viewModel: CustomizeViewModel = viewModel()) {
    val p = state.activeProduct ?: return
    val optionsResponse by viewModel.optionsResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOptions()
    }

    val extrasSum = state.selectedExtras.sumOf { it.price }
    val unitPrice = (p.price + state.selectedSize.price + extrasSum) * state.customQty

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { BackHeader("Personalizza Articolo") { state.goBackFromCustomize() } }
        errorMessage?.let { message ->
            item {
                ErrorBanner(message = message, onRetry = { viewModel.loadOptions() })
            }
        }
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

        // Sezione Dimensione
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("SCEGLI DIMENSIONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sizeOptions.forEach { size ->
                            val sel = state.selectedSize.id == size.id
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { state.selectedSize = size },
                                shape = RoundedCornerShape(12.dp),
                                color = if (sel) Brand.copy(alpha = 0.08f) else Slate100,
                                border = if (sel) androidx.compose.foundation.BorderStroke(2.dp, Brand) else null
                            ) {
                                Column(
                                    Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(size.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (sel) Brand else Slate700)
                                    if (size.price > 0)
                                        Text("+€${"%.2f".format(size.price)}", fontSize = 10.sp, color = Slate400)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sezione Ingredienti Extra
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("AGGIUNGI EXTRA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                    extraOptions.forEach { extra ->
                        val checked = state.selectedExtras.any { it.id == extra.id }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (checked) state.selectedExtras.removeAll { it.id == extra.id }
                                    else state.selectedExtras.add(extra)
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { chk ->
                                    if (chk) state.selectedExtras.add(extra)
                                    else state.selectedExtras.removeAll { it.id == extra.id }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Brand)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(extra.name, Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("+€${"%.2f".format(extra.price)}", fontSize = 11.sp, color = Slate400)
                        }
                    }
                }
            }
        }

        // Quantità e Pulsante Aggiungi al Carrello
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("QUANTITÀ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SmallQtyButton("-") { if (state.customQty > 1) state.customQty-- }
                        Text(
                            "${state.customQty}",
                            Modifier.width(36.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        SmallQtyButton("+") { state.customQty++ }
                    }
                }
            }
        }

        item {
            Button(
                onClick = { state.confirmAddToCart() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(Brand)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Aggiungi al Carrello", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("€${"%.2f".format(unitPrice)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }
        }
    }
}
