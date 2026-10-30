package com.example.foodapp.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.ui.Theme.FastBiteRed
import com.example.foodapp.ui.Theme.FastBiteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isLoggedIn: Boolean = false,
    preferredStore: String = "FastBite Milano Centro",
    onLogin: () -> Unit = {},
    onStore: () -> Unit = {},
    onPayments: () -> Unit = {},
    onHistory: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Il Mio Profilo FastBite", fontWeight = FontWeight.Bold) })
    }) { padding ->
        Column(Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (!isLoggedIn) {
                // Login card
                Card(Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(shape = CircleShape, color = FastBiteRed.copy(alpha = 0.1f),
                            modifier = Modifier.size(80.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = FastBiteRed,
                                    modifier = Modifier.size(40.dp))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Accedi a FastBite", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Salva i tuoi ordini e punti vendita preferiti",
                            color = Color.Gray, fontSize = 13.sp)
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
                            Text("🔵 Continua con Google")
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = FastBiteRed)) {
                            Text("Accedi / Registrati", color = Color.White)
                        }
                    }
                }
            } else {
                // Profile info
                Card(Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = FastBiteRed,
                            modifier = Modifier.size(60.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("MR", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Mario Rossi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("mario.rossi@email.com", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Menu items
            ProfileMenuItem(Icons.Default.Store, "Punto Vendita Preferito", preferredStore, onStore)
            ProfileMenuItem(Icons.Default.CreditCard, "Metodi di Pagamento Salvati", "Visa •••• 4242", onPayments)
            ProfileMenuItem(Icons.Default.History, "Cronologia Ordini Asporto", "3 ordini", onHistory)

            if (isLoggedIn) {
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onLogout, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text("Disconnetti Profilo", color = FastBiteRed)
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(subtitle, color = Color.Gray, fontSize = 13.sp) },
        leadingContent = {
            Surface(shape = CircleShape, color = FastBiteRed.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = FastBiteRed, modifier = Modifier.size(20.dp))
                }
            }
        },
        trailingContent = { Icon(Icons.Default.ChevronRight, null, tint = Color.Gray) },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Profile - Non loggato")
@Composable
private fun ProfileLoggedOutPreview() {
    FastBiteTheme { ProfileScreen(isLoggedIn = false) }
}

@Preview(showBackground = true, showSystemUi = true, name = "Profile - Loggato")
@Composable
private fun ProfileLoggedInPreview() {
    FastBiteTheme { ProfileScreen(isLoggedIn = true) }
}