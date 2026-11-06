package com.example.foodapp.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.AppState
import com.example.foodapp.Emerald
import com.example.foodapp.EmeraldBg
import com.example.foodapp.Slate400
import com.example.foodapp.Slate700
import com.example.foodapp.Slate900
import com.example.foodapp.ui.components.BackHeader

@Composable
fun PaymentHistoryScreen(state: AppState, viewModel: ProfileViewModel = viewModel()) {

    val paymentHystory by viewModel.paymentHistory.collectAsState()

    LaunchedEffect(state.userId) {
        viewModel.loadProfileData(state.userId ?: "user_123")
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BackHeader("Storico Pagamenti") { state.screen = "profile" }
        }
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Slate900),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "PAGAMENTI EFFETTUATI",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "€${"%.2f".format(paymentHystory.sumOf { it.amount })}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        "${paymentHystory.size} transazioni registrate",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }
            }
        }
        if (paymentHystory.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💳", fontSize = 32.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Nessun pagamento registrato",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Slate700
                        )
                    }
                }
            }
        } else {
            items(paymentHystory) { payment ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    payment.id,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Surface(color = EmeraldBg, shape = RoundedCornerShape(50)) {
                                    Text(
                                        payment.status,
                                        Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald
                                    )
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                payment.paymentMethod,
                                fontSize = 11.sp,
                                color = Slate700,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(payment.date, fontSize = 10.sp, color = Slate400)
                        }
                        Text(
                            "-€${"%.2f".format(payment.amount)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Slate900
                        )
                    }
                }
            }
        }
    }
}
