package com.example.foodapp.ui.customize

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Slate100
import com.example.foodapp.Slate500
import com.example.foodapp.ui.components.BackHeader

@Composable
fun CustomizeScreen(state: AppState, viewModel: CustomizeViewModel = viewModel()) {
    val p = state.activeProduct ?: return
    val optionsResponse by viewModel.optionsResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOptions()
    }

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
        if (isLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else {
            items(optionsResponse) { optionGroupRes ->
                Column(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("OPZIONI DAL SERVER (ID: ${optionGroupRes.productId})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Brand)
                    optionGroupRes.groups.forEach { group ->
                        Text(group.groupName, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                        group.choises.forEach { choice ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(choice.name, fontSize = 11.sp)
                                Text("+€${choice.priceModifier}", fontSize = 11.sp, color = Brand)
                            }
                        }
                    }
                }
            }
        }
    }
}
