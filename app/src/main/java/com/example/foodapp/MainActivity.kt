package com.example.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// ============================================================
//  FastBite — app d'asporto (Jetpack Compose, un solo file)
// ============================================================

// ---------- Palette ----------
private val Brand = Color(0xFFFF4757)
private val BrandDark = Color(0xFFC12332)
private val Slate900 = Color(0xFF0F172A)
private val Slate800 = Color(0xFF1E293B)
private val Slate50 = Color(0xFFF8FAFC)
private val Slate100 = Color(0xFFF1F5F9)
private val Slate200 = Color(0xFFE2E8F0)
private val Slate400 = Color(0xFF94A3B8)
private val Slate500 = Color(0xFF64748B)
private val Slate700 = Color(0xFF334155)
private val Emerald = Color(0xFF059669)
private val EmeraldBg = Color(0xFFD1FAE5)

// ---------- Modelli ----------
data class Store(
    val id: Int, val name: String, val address: String,
    val distance: String, val prepTime: String, val isNearest: Boolean
)

data class CategoryItem(val id: String, val name: String, val emoji: String)

data class Product(
    val id: Int, val category: String, val name: String,
    val description: String, val price: Double, val emoji: String
)

data class SizeOption(val id: Int, val name: String, val price: Double)
data class ExtraOption(val id: Int, val name: String, val price: Double)

data class CartItem(
    val product: Product, val size: SizeOption,
    val extras: List<ExtraOption>, val quantity: Int
) {
    val itemTotal: Double get() = product.price + size.price + extras.sumOf { it.price }
}

// ---------- Dati ----------
val stores = listOf(
    Store(1, "FastBite - Via Roma", "Via Roma 42, Milano", "350m", "10-15 min", true),
    Store(2, "FastBite - Duomo", "Piazza del Duomo 8, Milano", "1.2 km", "15-20 min", false),
    Store(
        3,
        "FastBite - Navigli",
        "Ripa di Porta Ticinese 15, Milano",
        "2.5 km",
        "20-25 min",
        false
    )
)

val categories = listOf(
    CategoryItem("burgers", "Burgers", "\uD83C\uDF54"),
    CategoryItem("pizzas", "Pizze", "\uD83C\uDF55"),
    CategoryItem("drinks", "Bibite", "\uD83E\uDDC3"),
    CategoryItem("desserts", "Dolci", "\uD83C\uDF66")
)

val products = listOf(
    Product(
        101,
        "burgers",
        "Fast Bacon Burger",
        "Manzo 180g, bacon croccante, cheddar, salsa special.",
        9.50,
        "\uD83C\uDF54"
    ),
    Product(
        102,
        "burgers",
        "Double Cheese Monster",
        "Doppio burger, doppio cheddar melted, cipolla caramellata.",
        11.50,
        "\uD83C\uDF54"
    ),
    Product(
        103,
        "pizzas",
        "Pizza Margherita DOC",
        "Pomodoro bio, mozzarella di bufala, basilico fresco.",
        7.50,
        "\uD83C\uDF55"
    ),
    Product(
        104,
        "pizzas",
        "Diavola Spicy Special",
        "Pomodoro, mozzarella, salame piccante, olio al peperoncino.",
        8.50,
        "\uD83C\uDF55"
    ),
    Product(
        105,
        "drinks",
        "Coca Cola Zero 33cl",
        "Lattina da 33cl servita fredda.",
        2.50,
        "\uD83E\uDDC3"
    ),
    Product(
        106,
        "desserts",
        "Tiramisù della Casa",
        "Mascarpone, savoiardi al caffè, cacao amaro.",
        4.50,
        "\uD83C\uDF66"
    )
)

val sizeOptions = listOf(SizeOption(1, "Standard", 0.0), SizeOption(2, "XXL Big", 2.50))
val extraOptions = listOf(
    ExtraOption(1, "Extra Cheddar", 1.0),
    ExtraOption(2, "Bacon Croccante", 1.5),
    ExtraOption(3, "Salsa BBQ", 0.5)
)

// ---------- Stato ----------
class AppState {
    var screen by mutableStateOf("splash")
    var previousScreen by mutableStateOf("home")
    var isLoggedIn by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("all")
    var activeQuickCat by mutableStateOf("all")
    var promoInput by mutableStateOf("")
    var promoApplied by mutableStateOf(false)
    var pickupTime by mutableStateOf("ASAP")
    var paymentMethod by mutableStateOf("card")
    var orderId by mutableStateOf("")
    var pickupPin by mutableStateOf("")
    var selectedStore by mutableStateOf(stores[0])
    var activeProduct by mutableStateOf<Product?>(null)
    var selectedSize by mutableStateOf(sizeOptions[0])
    val selectedExtras = mutableStateListOf<ExtraOption>()
    var customQty by mutableStateOf(1)
    val cart = mutableStateListOf<CartItem>().apply {
        add(CartItem(products[0], sizeOptions[0], listOf(extraOptions[1]), 1))
    }

    val cartSubtotal: Double get() = cart.sumOf { it.itemTotal * it.quantity }
    val discountAmount: Double get() = if (promoApplied) cartSubtotal * 0.10 else 0.0
    val cartFinalTotal: Double get() = maxOf(0.0, cartSubtotal - discountAmount + 0.50)
    val cartTotalCount: Int get() = cart.sumOf { it.quantity }

    fun goBackFromCustomize() {
        screen = if (previousScreen == "add-more") "summary" else previousScreen
    }

    fun openCustomize(p: Product) {
        previousScreen = screen
        activeProduct = p
        selectedSize = sizeOptions[0]
        selectedExtras.clear()
        customQty = 1
        screen = "item-customize"
    }

    fun confirmAddToCart() {
        val p = activeProduct ?: return
        cart.add(CartItem(p, selectedSize, selectedExtras.toList(), customQty))
        screen = if (previousScreen == "add-more") "summary" else previousScreen
    }

    fun applyPromo() {
        if (promoInput.trim().uppercase() == "FAST10") promoApplied = true
    }

    fun proceedToCheckout() {
        screen = if (isLoggedIn) "checkout" else "auth"
    }

    fun simulateLogin() {
        isLoggedIn = true; screen = "checkout"
    }

    fun confirmOrder() {
        orderId = "#${Random.nextInt(1000, 10000)}"
        pickupPin = "PIN-${Random.nextInt(100, 1000)}"
        screen = "confirmation"
    }

    fun resetApp() {
        cart.clear(); promoApplied = false; promoInput = ""; screen = "home"
    }
}

// ============================================================
//  MainActivity
// ============================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Brand, surface = Color.White)) {
                val state = remember { AppState() }
                FastBiteApp(state)
            }
        }
    }
}

// ============================================================
//  Struttura principale
// ============================================================
@Composable
fun FastBiteApp(state: AppState) {
    val navScreens = listOf("home", "search", "categories", "cart", "profile")

    Scaffold(
        containerColor = Slate50,
        topBar = { if (state.screen in navScreens) Header(state) },
        bottomBar = { if (state.screen in navScreens) BottomBar(state) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (state.screen) {
                "splash" -> SplashScreen { state.screen = "home" }
                "home" -> HomeScreen(state)
                "search" -> SearchScreen(state)
                "categories" -> CategoriesScreen(state)
                "cart" -> CartScreen(state)
                "checkout" -> CheckoutScreen(state)
                "summary" -> SummaryScreen(state)
                "add-more" -> AddMoreScreen(state)
                "item-customize" -> CustomizeScreen(state)
                "confirmation" -> ConfirmationScreen(state)
                "profile" -> ProfileScreen(state)
                "store-select" -> StoreSelectScreen(state)
                "auth" -> AuthScreen(state)
            }
        }
    }
}

// ============================================================
//  Header e Bottom Bar
// ============================================================
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
                    .clickable { state.screen = "store-select" }
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
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Slate100)
                    .clickable { state.screen = if (state.isLoggedIn) "profile" else "auth" },
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoggedIn) Text(
                    "M", color = Color.White, fontWeight = FontWeight.Bold,
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

// ============================================================
//  Schermata 1 — Splash
// ============================================================
@Composable
fun SplashScreen(onStart: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Brand, BrandDark))
            )
            .padding(32.dp)
    ) {
        Column(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) { Text("\uD83C\uDF54", fontSize = 40.sp) }
            Spacer(Modifier.height(12.dp))
            Text("FastBite", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Text(
                "Ordina in app. Salta la fila. Ritira caldo in cassa.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
        Column(Modifier.align(Alignment.BottomCenter)) {
            Card(
                colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.border(
                    1.dp,
                    Color.White.copy(alpha = 0.3f),
                    RoundedCornerShape(20.dp)
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Zero costi di consegna o commissioni rider",
                        "Sedi selezionate con indicazioni mappa",
                        "Pass QR code rapido per ritiro espresso"
                    ).forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint = EmeraldBg,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(it, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(Color.White, contentColor = Brand)
            ) { Text("Inizia ad Ordinare", fontWeight = FontWeight.ExtraBold) }
        }
    }
}

// ============================================================
//  Schermata 2 — Home
// ============================================================
@Composable
fun HomeScreen(state: AppState) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Banner punto vendita
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(Slate900),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Surface(color = Brand.copy(alpha = 0.3f), shape = RoundedCornerShape(50)) {
                            Text(
                                "SOLO ASPORTO",
                                Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand.copy(alpha = 0.9f)
                            )
                        }
                        Text(
                            "\uD83D\uDCCD ${state.selectedStore.distance}",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        state.selectedStore.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(state.selectedStore.address, color = Slate400, fontSize = 11.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            "\u23F1 Pronto in: ${state.selectedStore.prepTime}",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                        Text("Ritiro: ", color = Slate400, fontSize = 11.sp)
                    }
                }
            }
        }
        // Categorie
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Categorie", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Text(
                        "Vedi tutte", color = Brand, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { state.screen = "categories" })
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(categories) { cat ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                            color = Color.White,
                            modifier = Modifier.clickable {
                                state.selectedCategory = cat.id; state.screen = "categories"
                            }
                        ) {
                            Row(
                                Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.emoji, fontSize = 16.sp)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    cat.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate700
                                )
                            }
                        }
                    }
                }
            }
        }
        // Più ordinati
        item {
            Text(
                "I Più Ordinati da Asporto",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
        }
        items(products.take(4)) { p -> ProductCard(p, onCustomize = { state.openCustomize(p) }) }
    }
}

@Composable
fun ProductCard(p: Product, onCustomize: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(12.dp)) {
            Box(
                Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Slate100),
                contentAlignment = Alignment.Center
            ) { Text(p.emoji, fontSize = 32.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        p.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "€${"%.2f".format(p.price)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    p.description, fontSize = 11.sp, color = Slate500, maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onCustomize,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(Brand.copy(alpha = 0.12f), Brand),
                        elevation = null,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) { Text("Personalizza", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

// ============================================================
//  Schermata 3 — Cerca
// ============================================================
@Composable
fun SearchScreen(state: AppState) {
    val result = if (state.searchQuery.isBlank()) products
    else products.filter { it.name.lowercase().contains(state.searchQuery.lowercase()) }
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
                value = state.searchQuery,
                onValueChange = { state.searchQuery = it },
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
        items(result) { p ->
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

// ============================================================
//  Schermata 4 — Menu / Categorie
// ============================================================
@Composable
fun CategoriesScreen(state: AppState) {
    val filtered = if (state.selectedCategory == "all") products
    else products.filter { it.category == state.selectedCategory }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Menu Completo", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text("${filtered.size} articoli", color = Slate400, fontSize = 11.sp)
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.selectedCategory == "all",
                        onClick = { state.selectedCategory = "all" },
                        label = { Text("Tutti", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand, selectedLabelColor = Color.White
                        )
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = state.selectedCategory == cat.id,
                        onClick = { state.selectedCategory = cat.id },
                        label = { Text("${cat.emoji} ${cat.name}", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand, selectedLabelColor = Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        items(filtered) { p -> ProductCard(p, onCustomize = { state.openCustomize(p) }) }
    }
}

// ============================================================
//  Schermata 5 — Carrello
// ============================================================
@Composable
fun CartScreen(state: AppState) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Il Tuo Carrello", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Surface(color = EmeraldBg, shape = RoundedCornerShape(50)) {
                    Text(
                        "Ritiro Gratuito", Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Emerald
                    )
                }
            }
        }
        if (state.cart.isEmpty()) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("\uD83D\uDECD", fontSize = 48.sp)
                    Text("Il tuo carrello di asporto è vuoto", color = Slate400, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { state.screen = "categories" },
                        colors = ButtonDefaults.buttonColors(Brand)
                    ) { Text("Sfoglia Menu") }
                }
            }
        }
        items(state.cart.size) { i ->
            val item = state.cart[i]
            Card(
                shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Slate100),
                                contentAlignment = Alignment.Center
                            ) { Text(item.product.emoji, fontSize = 22.sp) }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    item.product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    item.size.name + if (item.extras.isNotEmpty()) " • " +
                                            item.extras.joinToString(", ") { it.name } else "",
                                    fontSize = 10.sp,
                                    color = Slate400,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Text(
                            "€${"%.2f".format(item.itemTotal * item.quantity)}",
                            fontWeight = FontWeight.ExtraBold, fontSize = 12.sp
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { state.cart.removeAt(i) }) {
                            Icon(
                                Icons.Default.Delete,
                                null,
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SmallQtyButton("-") {
                                val q = item.quantity - 1
                                if (q <= 0) state.cart.removeAt(i)
                                else state.cart[i] = item.copy(quantity = q)
                            }
                            Text(
                                "${item.quantity}",
                                Modifier.width(28.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            SmallQtyButton("+") {
                                state.cart[i] = item.copy(quantity = item.quantity + 1)
                            }
                        }
                    }
                }
            }
        }
        if (state.cart.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "CODICE SCONTO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400
                        )
                        Row {
                            OutlinedTextField(
                                value = state.promoInput,
                                onValueChange = { state.promoInput = it },
                                placeholder = { Text("Inserisci es. FAST10", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Brand)
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { state.applyPromo() },
                                colors = ButtonDefaults.buttonColors(Slate900),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Applica", fontSize = 11.sp) }
                        }
                        if (state.promoApplied)
                            Text(
                                "Sconto 10% applicato all'asporto!", fontSize = 10.sp,
                                fontWeight = FontWeight.Bold, color = Emerald
                            )
                    }
                }
            }
            item { TotalsCard(state) }
            item {
                Button(
                    onClick = { state.proceedToCheckout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(Brand)
                ) {
                    Row(
                        Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Procedi al Checkout", fontWeight = FontWeight.Bold)
                        Text(
                            "€${"%.2f".format(state.cartFinalTotal)}",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TotalsCard(state: AppState) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            TotalsRow("Subtotale articoli", "€${"%.2f".format(state.cartSubtotal)}")
            if (state.promoApplied) TotalsRow(
                "Sconto FAST10 (-10%)",
                "-€${"%.2f".format(state.discountAmount)}",
                Emerald
            )
            TotalsRow("Costo gestione asporto", "€0.50")
            TotalsRow("Ritiro in cassa", "Gratuito", Emerald)
            HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Totale Finale", fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text(
                    "€${"%.2f".format(state.cartFinalTotal)}", fontWeight = FontWeight.Black,
                    fontSize = 14.sp, color = Brand
                )
            }
        }
    }
}

@Composable
fun TotalsRow(label: String, value: String, color: Color = Slate700) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = Slate500)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun SmallQtyButton(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Slate100)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700) }
}

// ============================================================
//  Schermata 6 — Checkout
// ============================================================
@Composable
fun CheckoutScreen(state: AppState) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackHeader("Checkout & Pagamento") { state.screen = "cart" } }
        // Locale + mappa
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(
                                "LOCALE DI RITIRO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate400
                            )
                            Text(
                                state.selectedStore.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(state.selectedStore.address, fontSize = 11.sp, color = Slate500)
                        }
                        Text(
                            "Cambia", color = Brand, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { state.screen = "store-select" })
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate800),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("\uD83D\uDCCD", fontSize = 24.sp)
                            Surface(color = Slate900, shape = RoundedCornerShape(50)) {
                                Text(
                                    state.selectedStore.name,
                                    Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
        // Orario
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "ORARIO DI RITIRO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TimeChip(
                            "Subito",
                            "~ ${state.selectedStore.prepTime}",
                            state.pickupTime == "ASAP"
                        ) {
                            state.pickupTime = "ASAP"
                        }
                        TimeChip(
                            "Programmato",
                            "12:45",
                            state.pickupTime == "12:45"
                        ) { state.pickupTime = "12:45" }
                        TimeChip(
                            "Programmato",
                            "13:15",
                            state.pickupTime == "13:15"
                        ) { state.pickupTime = "13:15" }
                    }
                }
            }
        }
        // Pagamento
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "METODO DI PAGAMENTO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    PayOption("card", "\uD83D\uDCB3 Carta di Credito / Google Pay", state)
                    PayOption("paypal", "\uD83D\uDCB8 PayPal", state)
                    PayOption("cash", "\uD83D\uDCB5 Paga in cassa al ritiro", state)
                }
            }
        }
        item {
            Button(
                onClick = { state.screen = "summary" },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(Slate900)
            ) {
                Text("Vai al Riepilogo Ordine", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TimeChip(label: String, value: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Brand else Slate100,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) Brand else Slate200),
        modifier = Modifier
            .width(24.dp)
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                label.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else Slate400
            )
            Text(
                value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                color = if (selected) Color.White else Slate700
            )
        }
    }
}

@Composable
fun PayOption(value: String, label: String, state: AppState) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (state.paymentMethod == value) Brand.copy(alpha = 0.08f) else Slate100)
            .clickable { state.paymentMethod = value }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = state.paymentMethod == value, onClick = { state.paymentMethod = value },
            colors = RadioButtonDefaults.colors(selectedColor = Brand)
        )
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
    }
}

// ============================================================
//  Schermata 7 — Riepilogo finale
// ============================================================
@Composable
fun SummaryScreen(state: AppState) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BackHeader("Verifica & Riepilogo Finale") { state.screen = "checkout" }
            Text(
                "Controlla che i dati siano corretti prima di procedere",
                fontSize = 10.sp, color = Slate400
            )
        }
        // Articoli
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader("Articoli Selezionati", "Modifica Carrello") {
                        state.screen = "cart"
                    }
                    state.cart.forEach { item ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = Slate100, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        "${item.quantity}x",
                                        Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        item.product.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(item.size.name, fontSize = 9.sp, color = Slate400)
                                }
                            }
                            Text(
                                "€${"%.2f".format(item.itemTotal * item.quantity)}",
                                fontSize = 12.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
                    // Aggiunta last-minute
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Brand.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brand.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { state.screen = "add-more" }
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                null,
                                tint = Brand,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "+ Aggiungi altri articoli al tuo ordine",
                                fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Brand
                            )
                        }
                    }
                }
            }
        }
        // Info ritiro
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionHeader("Info Ritiro", "Modifica") { state.screen = "checkout" }
                    InfoRow("Punto Vendita:", state.selectedStore.name)
                    InfoRow("Indirizzo:", state.selectedStore.address)
                    InfoRow(
                        "Orario Previsto:",
                        if (state.pickupTime == "ASAP") "Subito (${state.selectedStore.prepTime})" else state.pickupTime,
                        valueColor = Brand
                    )
                }
            }
        }
        // Pagamento
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionHeader("Metodo di Pagamento", "Modifica") { state.screen = "checkout" }
                    val label = when (state.paymentMethod) {
                        "card" -> "\uD83D\uDCB3 Carta di Credito / Google Pay"
                        "paypal" -> "\uD83D\uDCB8 PayPal"
                        else -> "\uD83D\uDCB5 Contanti al ritiro in cassa"
                    }
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                }
            }
        }
        item { TotalsCard(state) }
        item {
            Button(
                onClick = { state.confirmOrder() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(Brand)
            ) {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Conferma ed Effettua Ordine", fontWeight = FontWeight.Bold)
                    Text(
                        "€${"%.2f".format(state.cartFinalTotal)}",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Slate400)
        Text(
            action, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Brand,
            modifier = Modifier.clickable(onClick = onAction)
        )
    }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = Slate700) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 11.sp, color = Slate500)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

// ============================================================
//  Schermata 8 — Aggiungi al tuo ordine
// ============================================================
@Composable
fun AddMoreScreen(state: AppState) {
    val filtered = when (state.activeQuickCat) {
        "drinks" -> products.filter { it.category == "drinks" }
        "desserts" -> products.filter { it.category == "desserts" }
        else -> products.filter { it.category == "drinks" || it.category == "desserts" }
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BackHeader("Aggiungi al tuo Ordine") { state.screen = "summary" } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "all" to "Tutti",
                    "drinks" to "Bibite",
                    "desserts" to "Dolci"
                ).forEach { (id, label) ->
                    FilterChip(
                        selected = state.activeQuickCat == id,
                        onClick = { state.activeQuickCat = id },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brand, selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
        items(filtered) { p ->
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
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
                            p.description, fontSize = 10.sp, color = Slate400, maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "€${"%.2f".format(p.price)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Button(
                        onClick = { state.openCustomize(p) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(Brand),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("Seleziona", fontSize = 11.sp)
                    }
                }
            }
        }
        item {
            Button(
                onClick = { state.screen = "summary" },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(Slate900)
            ) { Text("Torna al Riepilogo") }
        }
    }
}

// ============================================================
//  Schermata 9 — Personalizzazione prodotto
// ============================================================
@Composable
fun CustomizeScreen(state: AppState) {
    val p = state.activeProduct ?: return
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
        // Dimensione
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "DIMENSIONE PORZIONE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sizeOptions.forEach { size ->
                            val sel = state.selectedSize.id == size.id
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (sel) Brand else Slate100,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { state.selectedSize = size }
                            ) {
                                Row(
                                    Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        size.name, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        color = if (sel) Color.White else Slate700
                                    )
                                    Text(
                                        if (size.price > 0) "+€${"%.2f".format(size.price)}" else "Incluso",
                                        fontSize = 11.sp,
                                        color = if (sel) Color.White.copy(alpha = 0.8f) else Slate500
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // Extra
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "AGGIUNGI EXTRA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                    extraOptions.forEach { extra ->
                        val checked = state.selectedExtras.any { it.id == extra.id }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate100)
                                .clickable {
                                    if (checked) state.selectedExtras.removeIf { it.id == extra.id }
                                    else state.selectedExtras.add(extra)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = checked, onCheckedChange = {
                                        if (it) state.selectedExtras.add(extra)
                                        else state.selectedExtras.removeIf { e -> e.id == extra.id }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = Brand)
                                )
                                Text(extra.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text(
                                "+€${"%.2f".format(extra.price)}", fontSize = 12.sp,
                                fontWeight = FontWeight.Bold, color = Slate500
                            )
                        }
                    }
                }
            }
        }
        // Quantità + conferma
        item {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, Slate200, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    SmallQtyButton("-") { state.customQty = maxOf(1, state.customQty - 1) }
                    Text(
                        "${state.customQty}", Modifier.width(28.dp), textAlign = TextAlign.Center,
                        fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                    SmallQtyButton("+") { state.customQty++ }
                }
                Button(
                    onClick = { state.confirmAddToCart() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(Brand),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        "Conferma • €${"%.2f".format(unitPrice * state.customQty)}",
                        fontWeight = FontWeight.Bold, fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ============================================================
//  Schermata 10 — Conferma ordine (pass ritiro)
// ============================================================
@Composable
fun ConfirmationScreen(state: AppState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(EmeraldBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, null, tint = Emerald, modifier = Modifier.size(28.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ordine Inviato con Successo!", fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(
                "Mostra il pass al banco quando l'ordine è pronto",
                fontSize = 11.sp, color = Slate500
            )
        }
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(Color.White),
            border = androidx.compose.foundation.BorderStroke(2.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "CODICE ORDINE RITIRO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )
                Text(state.orderId, fontSize = 26.sp, fontWeight = FontWeight.Black)
                Box(
                    Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Slate100),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "\u25A0\u25A1\u25A0\u25A1\u25A0",
                        fontSize = 22.sp,
                        color = Slate800,
                        lineHeight = 8.sp
                    )
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
                Text(
                    "PIN DI CHIAMATA CASSA",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400
                )
                Text(
                    state.pickupPin,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Brand
                )
            }
        }
        Card(
            shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Slate100),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "\uD83D\uDCCD ${state.selectedStore.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(state.selectedStore.address, fontSize = 11.sp, color = Slate500)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Orario Ritiro:", fontSize = 11.sp, color = Slate500)
                    Text(
                        if (state.pickupTime == "ASAP") state.selectedStore.prepTime else state.pickupTime,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Button(
            onClick = { state.resetApp() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(Slate900)
        ) { Text("Torna al Menu Principale") }
    }
}

// ============================================================
//  Schermata 11 — Profilo
// ============================================================
@Composable
fun ProfileScreen(state: AppState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Il Mio Profilo FastBite", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
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
                        if (state.isLoggedIn) "M" else "G", color = Color.White,
                        fontWeight = FontWeight.Black, fontSize = 16.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (state.isLoggedIn) "Marco Rossi" else "Utente Ospite",
                        fontWeight = FontWeight.Bold, fontSize = 13.sp
                    )
                    Text(
                        if (state.isLoggedIn) "marco.rossi@email.it" else "Accedi per salvare i locali preferiti",
                        fontSize = 11.sp, color = Slate500
                    )
                }
                if (!state.isLoggedIn)
                    TextButton(onClick = { state.screen = "auth" }) {
                        Text(
                            "Accedi",
                            color = Brand
                        )
                    }
            }
        }
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { state.screen = "store-select" }
                        .padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Store,
                            null,
                            tint = Brand,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Punto Vendita Preferito",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(state.selectedStore.name, fontSize = 11.sp, color = Slate400)
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Slate100)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountBox,
                            null,
                            tint = Brand,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Metodi di Pagamento Salvati",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Slate400)
                }
            }
        }
        if (state.isLoggedIn)
            OutlinedButton(
                onClick = { state.isLoggedIn = false },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Disconnetti Profilo") }
    }
}

// ============================================================
//  Schermata 12 — Selezione punto vendita
// ============================================================
@Composable
fun StoreSelectScreen(state: AppState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BackHeader("Seleziona Punto Vendita") { state.screen = "home" }
        stores.forEach { store ->
            val sel = state.selectedStore.id == store.id
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(if (sel) Brand.copy(alpha = 0.06f) else Color.White),
                border = androidx.compose.foundation.BorderStroke(
                    if (sel) 2.dp else 1.dp, if (sel) Brand else Slate200
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        state.selectedStore = store; state.screen = "home"
                    }
            ) {
                Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(store.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            if (store.isNearest) {
                                Spacer(Modifier.width(6.dp))
                                Surface(color = EmeraldBg, shape = RoundedCornerShape(50)) {
                                    Text(
                                        "Più vicino",
                                        Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald
                                    )
                                }
                            }
                        }
                        Text(store.address, fontSize = 11.sp, color = Slate500)
                        Text(
                            "\uD83D\uDCCD ${store.distance} • Pronto in ${store.prepTime}",
                            fontSize = 10.sp, color = Slate400
                        )
                    }
                    if (sel) Icon(Icons.Default.CheckCircle, null, tint = Brand)
                }
            }
        }
    }
}

// ============================================================
//  Schermata 13 — Autenticazione
// ============================================================
@Composable
fun AuthScreen(state: AppState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BackHeader("Accedi / Registrati") { state.screen = "home" }
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

// ---------- Utility ----------
@Composable
fun BackHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Slate700)
        }
        Spacer(Modifier.width(4.dp))
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}
