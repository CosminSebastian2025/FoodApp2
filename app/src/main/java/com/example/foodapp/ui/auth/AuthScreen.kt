package com.example.foodapp.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Slate400
import com.example.foodapp.ui.components.BackHeader

@Composable
fun AuthScreen(state: AppState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BackHeader("Accedi / Registrati") { state.screen = "profile" }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { state.simulateLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("G  Continua con Google", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Text(
            "oppure inserisci dati", fontSize = 10.sp, color = Slate400,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = "", onValueChange = {},
            placeholder = { Text("La tua email", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand)
        )
        OutlinedTextField(
            value = "", onValueChange = {},
            placeholder = { Text("Password", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand)
        )
        Button(
            onClick = { state.simulateLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(Brand)
        ) { Text("Accedi a FastBite", fontWeight = FontWeight.Bold) }
    }
}
