package com.example.foodapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Slate400
import com.example.foodapp.ui.components.BackHeader

@Composable
fun AuthScreen(state: AppState, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BackHeader("Accedi / Registrati") { state.screen = "profile" }
        Spacer(Modifier.height(8.dp))

        errorMessage?.let { err ->
            Text(err, color = Brand, fontSize = 12.sp)
        }

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
            value = email, onValueChange = { email = it },
            placeholder = { Text("La tua email", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand)
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            placeholder = { Text("Password", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand)
        )
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(email, password) {
                        state.simulateLogin()
                        state.screen = "profile"
                    }
                } else {
                    state.simulateLogin()
                    state.screen = "profile"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(Brand)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            } else {
                Text("Accedi a FastBite", fontWeight = FontWeight.Bold)
            }
        }
    }
}
