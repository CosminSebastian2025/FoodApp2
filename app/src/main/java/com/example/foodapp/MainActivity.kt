package com.example.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.ui.addmore.AddMoreScreen
import com.example.foodapp.ui.auth.AuthScreen
import com.example.foodapp.ui.cart.CartScreen
import com.example.foodapp.ui.categories.CategoriesScreen
import com.example.foodapp.ui.checkout.CheckoutScreen
import com.example.foodapp.ui.checkout.PaymentMethodSelectScreen
import com.example.foodapp.ui.confirmation.ConfirmationScreen
import com.example.foodapp.ui.customize.CustomizeScreen
import com.example.foodapp.ui.home.HomeScreen
import com.example.foodapp.ui.profile.OrderDetailScreen
import com.example.foodapp.ui.profile.PaymentHistoryScreen
import com.example.foodapp.ui.profile.ProfileScreen
import com.example.foodapp.ui.register.RegisterScreen
import com.example.foodapp.ui.search.SearchScreen
import com.example.foodapp.ui.settings.SettingsScreen
import com.example.foodapp.ui.settings.SettingsViewModel
import com.example.foodapp.ui.settings.UpdateAvailableBanner
import com.example.foodapp.ui.splash.SplashScreen
import com.example.foodapp.ui.store.StoreSelectScreen
import com.example.foodapp.ui.summary.SummaryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Brand, surface = Color.White)) {
                val appStateViewModel: AppStateViewModel = viewModel()
                FastBiteApp(appStateViewModel.state)
            }
        }
    }
}

@Composable
fun FastBiteApp(state: AppState) {
    val navScreens = listOf("home", "search", "categories", "cart", "profile")

    // ViewModel condiviso con la schermata Impostazioni: versione installata,
    // aggiornamento disponibile e progresso del download.
    val settingsViewModel: SettingsViewModel = viewModel()
    val settingsState by settingsViewModel.state.collectAsState()

    // Controllo silenzioso all'avvio: al massimo una volta al giorno.
    LaunchedEffect(Unit) {
        settingsViewModel.autoCheckIfDue()
    }

    // Senza questo, il tasto indietro chiude l'Activity invece di tornare alla schermata precedente.
    BackHandler(enabled = state.screen != "home" && state.screen != "splash") {
        state.goBack()
    }

    Scaffold(
        containerColor = Slate50,
        topBar = { if (state.screen in navScreens) Header(state) },
        bottomBar = { if (state.screen in navScreens) BottomBar(state) }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Striscia globale: visibile su tutte le schermate tranne Impostazioni e splash,
            // così un aggiornamento trovato all'avvio non passa inosservato.
            val update = settingsState.update
            if (update != null && state.screen != "settings" && state.screen != "splash") {
                UpdateAvailableBanner(update.versionName) { state.screen = "settings" }
            }
            Box(Modifier.weight(1f)) {
                when (state.screen) {
                    "splash" -> SplashScreen(state, onStart = { state.screen = "home" })
                    "home" -> HomeScreen(state)
                    "search" -> SearchScreen(state)
                    "categories" -> CategoriesScreen(state)
                    "cart" -> CartScreen(state)
                    "checkout" -> CheckoutScreen(state)
                    "summary" -> SummaryScreen(state)
                    "payment-select" -> PaymentMethodSelectScreen(state)
                    "add-more" -> AddMoreScreen(state)
                    "item-customize" -> CustomizeScreen(state)
                    "confirmation" -> ConfirmationScreen(state)
                    "profile" -> ProfileScreen(state)
                    // Stesso ViewModel usato per il controllo all'avvio: lo stato non si perde.
                    "settings" -> SettingsScreen(state, settingsViewModel)
                    "store-select" -> StoreSelectScreen(state)
                    "payment-history" -> PaymentHistoryScreen(state)
                    "order-detail" -> OrderDetailScreen(state)
                    "auth" -> AuthScreen(state)
                    "register" -> RegisterScreen(state)
                }
            }
        }
    }
}

@Composable
fun Header(state: AppState) {
    Surface(shadowElevation = 2.dp, color = Color.White) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selettore punto vendita
            Row(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        state.previousScreen = state.screen
                        state.screen = "store-select"
                    }
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Brand.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Store, null, tint = Brand, modifier = Modifier.size(18.dp)) }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("RITIRO IN NEGOZIO", fontSize = 10.sp, color = Slate500)
                    Text(
                        state.selectedStore.name, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
            }
            // Avatar / login
            val avatarInitial = state.userName.trim().take(1).uppercase()
                .ifBlank { state.userId.orEmpty().take(1).uppercase() }
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Slate100)
                    .clickable {
                        if (state.isLoggedIn) {
                            state.screen = "profile"
                        } else {
                            state.authReturnScreen = state.screen
                            state.screen = "auth"
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoggedIn) Text(
                    avatarInitial.ifBlank { "U" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brand, CircleShape)
                        .wrapContentSize(Alignment.Center)
                )
                else Icon(
                    Icons.Default.Person,
                    null,
                    tint = Slate700,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BottomBar(state: AppState) {
    val items = listOf(
        Triple("home", "Home", Icons.Default.Home),
        Triple("categories", "Menu", Icons.Default.Menu),
        Triple("search", "Cerca", Icons.Default.Search),
        Triple("cart", "Carrello", Icons.Default.ShoppingCart),
        Triple("profile", "Profilo", Icons.Default.Person)
    )
    NavigationBar(containerColor = Color.White) {
        items.forEach { (id, label, icon) ->
            NavigationBarItem(
                selected = state.screen == id,
                onClick = { state.screen = id },
                icon = {
                    BadgedBox(badge = {
                        if (id == "cart" && state.cartTotalCount > 0)
                            Badge { Text("${state.cartTotalCount}") }
                    }) { Icon(icon, contentDescription = label) }
                },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Brand, selectedTextColor = Brand,
                    unselectedIconColor = Slate400, unselectedTextColor = Slate400
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme(colorScheme = lightColorScheme(primary = Brand, surface = Color.White)) {
        val state = remember { AppState().apply { screen = "profile"; isLoggedIn = true } }
        ProfileScreen(state)
    }
}
