package com.example.foodapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodapp.ui.Theme.FastBiteRed
import com.example.foodapp.ui.cart.CartViewModel

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem("home", "Home", Icons.Default.Home),
    BottomNavItem("menu", "Menu", Icons.Default.RestaurantMenu),
    BottomNavItem("search", "Cerca", Icons.Default.Search),
    BottomNavItem("cart", "Carrello", Icons.Default.ShoppingCart),
    BottomNavItem("profile", "Profilo", Icons.Default.Person)
)

@Composable
fun FastBiteBottomBar(currentRoute: String?, cartVm: CartViewModel, onNavigate: (String) -> Unit) {
    val itemCount by cartVm.itemCount.collectAsState()
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    if (item.route == "cart" && itemCount > 0) {
                        BadgedBox(badge = { Badge(containerColor = FastBiteRed) { Text("$itemCount") } }) {
                            Icon(item.icon, item.label)
                        }
                    } else Icon(item.icon, item.label)
                },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FastBiteRed, selectedTextColor = FastBiteRed,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun EmptyStateView(icon: ImageVector, title: String, subtitle: String, actionLabel: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, modifier = Modifier.size(80.dp), tint = FastBiteRed.copy(alpha = 0.6f))
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, color = Color.Gray, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(24.dp))
        Button(onClick = onAction, shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FastBiteRed)) {
            Text(actionLabel, color = Color.White)
        }
    }
}