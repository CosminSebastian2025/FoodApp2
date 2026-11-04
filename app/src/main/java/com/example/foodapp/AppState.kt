package com.example.foodapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// ---------- Palette Colori del Brand ----------
val Brand = Color(0xFFFF4757)
val BrandDark = Color(0xFFC12332)
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate700 = Color(0xFF334155)
val Emerald = Color(0xFF059669)
val EmeraldBg = Color(0xFFD1FAE5)

// ---------- Modelli di Stato UI locali ----------
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

/** Elemento nel carrello dell'app (UI locale). */
data class CartItem(
    val product: Product, val size: SizeOption,
    val extras: List<ExtraOption>, val quantity: Int
) {
    val itemTotal: Double get() = product.price + size.price + extras.sumOf { it.price }
}

/** Storico ordini locale. */
data class OrderHistoryItem(
    val id: String,
    val date: String,
    val storeName: String,
    val itemsSummary: String,
    val total: Double,
    val storeAddress: String = "Via Roma 42, Milano",
    val orderType: String = "Asporto - Ritiro in Cassa",
    val paymentMethod: String = "💳 Carta di Credito",
    val status: String = "Completato",
    val pin: String = "PIN-782"
)

/** Storico pagamenti locale. */
data class PaymentHistoryItem(
    val id: String,
    val date: String,
    val amount: Double,
    val paymentMethod: String,
    val status: String = "Pagato"
)

// ---------- Dati Statici di Fallback ----------
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

/**
 * Gestore dello stato globale dell'applicazione e della navigazione tra le schermate (Composable).
 */
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
    var activeOrderHistoryItem by mutableStateOf<OrderHistoryItem?>(null)

    /** Apre il dettaglio di un ordine precedente. */
    fun openOrderDetail(item: OrderHistoryItem) {
        activeOrderHistoryItem = item
        screen = "order-detail"
    }

    val cart = mutableStateListOf<CartItem>()
    val orderHistory = mutableStateListOf<OrderHistoryItem>()
    val paymentHistory = mutableStateListOf<PaymentHistoryItem>()

    /** Calcola il subtotale del carrello. */
    val cartSubtotal: Double get() = cart.sumOf { it.itemTotal * it.quantity }

    /** Calcola lo sconto applicato tramite codice promozionale. */
    val discountAmount: Double get() = if (promoApplied) cartSubtotal * 0.10 else 0.0

    /** Calcola il totale finale del carrello inclusi costi di gestione. */
    val cartFinalTotal: Double get() = maxOf(0.0, cartSubtotal - discountAmount + 1.50)

    /** Conta il numero totale di articoli nel carrello. */
    val cartTotalCount: Int get() = cart.sumOf { it.quantity }

    /** Ritorna alla schermata precedente dopo la personalizzazione di un articolo. */
    fun goBackFromCustomize() {
        screen = if (previousScreen == "add-more") "summary" else previousScreen
    }

    /** Apre la schermata di personalizzazione per un prodotto. */
    fun openCustomize(p: Product) {
        previousScreen = screen
        activeProduct = p
        selectedSize = sizeOptions[0]
        selectedExtras.clear()
        customQty = 1
        screen = "item-customize"
    }

    /** Conferma l'aggiunta del prodotto personalizzato al carrello. */
    fun confirmAddToCart() {
        val p = activeProduct ?: return
        cart.add(CartItem(p, selectedSize, selectedExtras.toList(), customQty))
        screen = if (previousScreen == "add-more") "summary" else previousScreen
    }

    /** Applica un codice promozionale (es. FAST10). */
    fun applyPromo() {
        if (promoInput.trim().uppercase() == "FAST10") promoApplied = true
    }

    /** Avanza verso il riepilogo o l'autenticazione. */
    fun proceedToCheckout() {
        screen = if (isLoggedIn) "summary" else "auth"
    }

    /** Simula il login dell'utente. */
    fun simulateLogin() {
        isLoggedIn = true; screen = "profile"
    }

    /** Conferma l'ordine e lo aggiunge allo storico. */
    fun confirmOrder() {
        orderId = "#${Random.nextInt(1000, 10000)}"
        pickupPin = "PIN-${Random.nextInt(100, 1000)}"
        val summary =
            if (cart.isNotEmpty()) cart.joinToString(", ") { "${it.quantity}x ${it.product.name}" } else "Ordine FastBite"
        val timeStr = "Oggi, " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val payMethodLabel = when (paymentMethod) {
            "card" -> "💳 Carta di Credito / Google Pay"
            "paypal" -> "💸 PayPal"
            else -> "💵 Contanti al ritiro"
        }
        val newOrder = OrderHistoryItem(
            id = orderId,
            date = timeStr,
            storeName = selectedStore.name,
            storeAddress = selectedStore.address,
            orderType = "Asporto - Ritiro in Cassa ($pickupTime)",
            paymentMethod = payMethodLabel,
            itemsSummary = summary,
            total = cartFinalTotal,
            status = "In Preparazione",
            pin = pickupPin
        )
        orderHistory.add(0, newOrder)
        paymentHistory.add(
            0,
            PaymentHistoryItem("TRX-${Random.nextInt(1000, 9999)}", timeStr, cartFinalTotal, payMethodLabel)
        )
        screen = "confirmation"
    }

    /** Resetta lo stato dell'applicazione. */
    fun resetApp() {
        cart.clear(); promoApplied = false; promoInput = ""; screen = "home"
    }
}
