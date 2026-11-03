package com.example.foodapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500

@Composable
fun ProfileScreen(state: AppState, viewModel: ProfileViewModel = viewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    val orderHistory by viewModel.orderHistory.collectAsState()
    val paymentHistory by viewModel.paymentHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfileData("user_123")
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Il Mio Profilo FastBite", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
        if (isLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brand),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (state.isLoggedIn) (userProfile?.name?.take(1) ?: "M") else "G", color = Color.White,
                            fontWeight = FontWeight.Black, fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (state.isLoggedIn) (userProfile?.name ?: "Marco Rossi") else "Utente Ospite",
                            fontWeight = FontWeight.Bold, fontSize = 13.sp
                        )
                        Text(
                            if (state.isLoggedIn) (userProfile?.email ?: "marco.rossi@email.it") else "Accedi per salvare i locali preferiti",
                            fontSize = 11.sp, color = Slate500
                        )
                    }
                    if (!state.isLoggedIn)
                        TextButton(onClick = { state.screen = "auth" }) {
                            Text(
                                "Accedi",
                                color = Brand,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                }
            }
        }
        if (orderHistory.isNotEmpty()) {
            item {
                Text("Storico Ordini dal Server", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            items(orderHistory) { order ->
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ordine ${order.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("€${"%.2f".format(order.total)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Brand)
                        }
                        Text(order.itemsSummary, fontSize = 11.sp, color = Slate500)
                        Text("${order.date} • ${order.status}", fontSize = 10.sp, color = Slate400)
                    }
                }
            }
        }
    }
}
