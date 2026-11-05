package com.example.foodapp.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import com.example.foodapp.Slate200
import com.example.foodapp.Slate400
import com.example.foodapp.products

@Composable
fun SearchScreen(state: AppState, viewModel: SearchViewModel = viewModel()) {
    var query by remember { mutableStateOf(state.searchQuery) }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            viewModel.search(query)
        }
    }

    val displayList = if (query.isBlank()) {
        products
    } else if (searchResults.isNotEmpty()) {
        searchResults.mapNotNull { p ->
            com.example.foodapp.Product(
                id = p.IdProduct?.takeIf { it.isNotBlank() } ?: return@mapNotNull null,
                category = p.category,
                name = p.nameProduct,
                description = p.description,
                price = p.basePrice,
                emoji = "🍔"
            )
        }
    } else {
        products.filter { it.name.lowercase().contains(query.lowercase()) || it.description.lowercase().contains(query.lowercase()) }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Cerca nel Menu", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    state.searchQuery = it
                },
                placeholder = {
                    Text(
                        "Cerca burger, pizze, bibite...",
                        fontSize = 12.sp,
                        color = Slate400
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        null,
                        modifier = Modifier.size(18.dp),
                        tint = Slate400
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Brand,
                    unfocusedBorderColor = Slate200
                )
            )
        }
        if (isLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else {
            items(displayList) { p ->
                Card(
                    shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate100),
                            contentAlignment = Alignment.Center
                        ) { Text(p.emoji, fontSize = 26.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(p.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                "€${"%.2f".format(p.price)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { state.openCustomize(p) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(Brand),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
