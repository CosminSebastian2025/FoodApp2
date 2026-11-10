package com.example.foodapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.foodapp.data.SessionStore
import com.example.foodapp.data.model.OrderHistoryItemResponse

// Typealias per garantire compatibilità trasparente con tutte le schermate UI
typealias Product = com.example.foodapp.data.model.Product

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
    val id: String, val name: String, val address: String,
    val distance: String, val prepTime: String, val isNearest: Boolean
)

data class CategoryItem(
    val id: String,
    val name: String,
    val emoji: String
)

data class SizeOption(val id: Int, val name: String, val price: Double)
data class ExtraOption(val id: Int, val name: String, val price: Double)

/** Elemento nel carrello dell'app (UI locale). */
data class CartItem(
    val product: Product, val size: SizeOption,
    val extras: List<ExtraOption>, val quantity: Int
) {
    val itemTotal: Double get() = product.basePrice + size.price + extras.sumOf { it.price }
}

/** Storico ordini locale. */
//data class OrderHistoryItem(
//    val id: String,
//    val date: String,
//    val storeName: String,
//    val itemsSummary: String,
//    val total: Double,
//    val storeAddress: String = "Via Roma 42, Milano",
//    val orderType: String = "Asporto - Ritiro in Cassa",
//    val paymentMethod: String = "💳 Carta di Credito",
//    val status: String = "Completato",
//    val pin: String = "PIN-782"
//)

/** Storico pagamenti locale. */
data class PaymentHistoryItem(
    val id: String,
    val date: String,
    val amount: Double,
    val paymentMethod: String,
    val status: String = "Pagato"
)

// ---------- Dati Statici di Fallback ----------
//val stores = listOf(
//    Store(1, "FastBite - Via Roma", "Via Roma 42, Milano", "350m", "10-15 min", true),
//    Store(2, "FastBite - Duomo", "Piazza del Duomo 8, Milano", "1.2 km", "15-20 min", false),
//    Store(
//        3,
//        "FastBite - Navigli",
//        "Ripa di Porta Ticinese 15, Milano",
//        "2.5 km",
//        "20-25 min",
//        false
//    )
//)

//val categories = listOf(
//    CategoryItem("burgers", "Burgers", "\uD83C\uDF54"),
//    CategoryItem("pizzas", "Pizze", "\uD83C\uDF55"),
//    CategoryItem("drinks", "Bibite", "\uD83E\uDDC3"),
//    CategoryItem("desserts", "Dolci", "\uD83C\uDF66")
//)

//val products = listOf(
//    Product(
//        IdProduct = "101",
//        category = "burgers",
//        nameProduct = "Fast Bacon Burger",
//        description = "Manzo 180g, bacon croccante, cheddar, salsa special.",
//        basePrice = 9.50,
//        imageUrl = "\uD83C\uDF54",
//        isPopular = true
//    ),
//    Product(
//        IdProduct = "102",
//        category = "burgers",
//        nameProduct = "Double Cheese Monster",
//        description = "Doppio burger, doppio cheddar melted, cipolla caramellata.",
//        basePrice = 11.50,
//        imageUrl = "\uD83C\uDF54",
//        isPopular = true
//    ),
//    Product(
//        IdProduct = "103",
//        category = "pizzas",
//        nameProduct = "Pizza Margherita DOC",
//        description = "Pomodoro bio, mozzarella di bufala, basilico fresco.",
//        basePrice = 7.50,
//        imageUrl = "\uD83C\uDF55",
//        isPopular = true
//    ),
//    Product(
//        IdProduct = "104",
//        category = "pizzas",
//        nameProduct = "Diavola Spicy Special",
//        description = "Pomodoro, mozzarella, salame piccante, olio al peperoncino.",
//        basePrice = 8.50,
//        imageUrl = "\uD83C\uDF55",
//        isPopular = false
//    ),
//    Product(
//        IdProduct = "105",
//        category = "drinks",
//        nameProduct = "Coca Cola Zero 33cl",
//        description = "Lattina da 33cl servita fredda.",
//        basePrice = 2.50,
//        imageUrl = "\uD83E\uDDC3",
//        isPopular = false
//    ),
//    Product(
//        IdProduct = "106",
//        category = "desserts",
//        nameProduct = "Tiramisù della Casa",
//        description = "Mascarpone, savoiardi al caffè, cacao amaro.",
//        basePrice = 4.50,
//        imageUrl = "\uD83C\uDF66",
//        isPopular = false
//    )
//)

/** Costo di gestione dell'asporto, allineato al valore del server (cart_rules.managementFee). */
const val MANAGEMENT_FEE = 0.50

val sizeOptions = listOf(SizeOption(1, "Standard", 0.0), SizeOption(2, "XXL Big", 2.50))
val extraOptions = listOf(
    ExtraOption(1, "Extra Cheddar", 1.0),
    ExtraOption(2, "Bacon Croccante", 1.5),
    ExtraOption(3, "Salsa BBQ", 0.5)
)

/**
 * Gestore dello stato globale dell'applicazione e della navigazione tra le schermate (Composable).
 */
class AppState(private val sessionStore: SessionStore? = null) {
    var screen by mutableStateOf("splash")
    var previousScreen by mutableStateOf("home")
    var isLoggedIn by mutableStateOf(false)
    var userId by mutableStateOf<String?>(null)
    var userName by mutableStateOf("")
    var userEmail by mutableStateOf("")

    init {
        val savedId = sessionStore?.userId().orEmpty()
        if (savedId.isNotBlank()) {
            userId = savedId
            userName = sessionStore?.name().orEmpty()
            userEmail = sessionStore?.email().orEmpty()
            isLoggedIn = true
        }
    }
    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("all")
    var activeQuickCat by mutableStateOf("all")
    var promoInput by mutableStateOf("")
    var promoApplied by mutableStateOf(false)
    var pickupTime by mutableStateOf("ASAP")
    var paymentMethod by mutableStateOf("card")
    var orderId by mutableStateOf("")
    var pickupPin by mutableStateOf("")
    var authMessage by mutableStateOf<String?>(null)
    var authReturnScreen by mutableStateOf("profile")
    var checkoutError by mutableStateOf<String?>(null)

    var selectedStore by mutableStateOf(
        Store("", "Seleziona punto vendita", "", "-", "-", false)
    )
    var activeProduct by mutableStateOf<Product?>(null)
    var selectedSize by mutableStateOf(sizeOptions[0])
    val selectedExtras = mutableStateListOf<ExtraOption>()
    var customQty by mutableStateOf(1)
    var activeOrderHistoryItem by mutableStateOf<OrderHistoryItemResponse?>(null)

    /** Ritorna alla schermata che aveva richiesto il login (checkout, riepilogo, ecc.). */
    fun consumeAuthReturnScreen(): String {
        val destination = authReturnScreen
        authReturnScreen = "profile"
        return destination
    }

    /** Avvia la sessione e la salva su disco, così sopravvive alla chiusura dell'app. */
    fun startSession(userId: String, name: String, email: String) {
        this.userId = userId
        this.userName = name
        this.userEmail = email
        this.isLoggedIn = true
        sessionStore?.save(userId, name, email)
    }

    /** Aggiorna nome/email con i dati del server e li rende persistenti. */
    fun updateProfile(name: String, email: String) {
        if (name.isNotBlank()) userName = name
        if (email.isNotBlank()) userEmail = email
        val id = userId
        if (!id.isNullOrBlank()) sessionStore?.save(id, userName, userEmail)
    }

    /** Chiude la sessione e cancella i dati salvati. */
    fun endSession() {
        isLoggedIn = false
        userId = null
        userName = ""
        userEmail = ""
        sessionStore?.clear()
    }

    /** Gestisce il tasto indietro di sistema senza chiudere l'app. */
    fun goBack() {
        screen = when (screen) {
            "order-detail", "payment-history", "settings" -> "profile"
            "auth" -> consumeAuthReturnScreen()
            "register" -> "auth"
            "store-select" -> if (previousScreen == "store-select") "home" else previousScreen
            "checkout", "summary" -> "cart"
            "payment-select" -> "summary"
            "add-more" -> "summary"
            "item-customize" -> if (previousScreen == "add-more") "summary" else previousScreen
            "confirmation" -> "home"
            else -> "home"
        }
    }

    /** Apre il dettaglio di un ordine precedente. */
    fun openOrderDetail(item: OrderHistoryItemResponse) {
        activeOrderHistoryItem = item
        screen = "order-detail"
    }

    val cart = mutableStateListOf<CartItem>()
//    val orderHistory = mutableStateListOf<OrderHistoryItem>()
//    val paymentHistory = mutableStateListOf<PaymentHistoryItem>()

    /** Calcola il subtotale del carrello. */
    val cartSubtotal: Double get() = cart.sumOf { it.itemTotal * it.quantity }

    /** Calcola lo sconto applicato tramite codice promozionale. */
    val discountAmount: Double get() = if (promoApplied) cartSubtotal * 0.10 else 0.0

    /** Calcola il totale finale del carrello inclusi costi di gestione. */
    val cartFinalTotal: Double get() = maxOf(0.0, cartSubtotal - discountAmount + MANAGEMENT_FEE)

    /** Conta il numero totale di articoli nel carrello. */
    val cartTotalCount: Int get() = cart.sumOf { it.quantity }

    /** Ritorna alla schermata precedente dopo la personalizzazione di un articolo. */
    fun goBackFromCustomize() {
        screen = if (previousScreen == "add-more") "summary" else previousScreen
    }

    /** Apre la schermata di personalizzazione per un prodotto. */
    fun openCustomize(p: Product?) {
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

    /** Applica un codice promozionale (FAST10 o BENVENUTO10). */
    fun applyPromo() {
        val code = promoInput.trim().uppercase()
        if (code == "FAST10" || code == "BENVENUTO10") promoApplied = true
    }

    /** Avanza verso il riepilogo o l'autenticazione. */
    fun proceedToCheckout() {
        screen = if (isLoggedIn) "summary" else "auth"
    }

//    /** Simula il login dell'utente. */
//    fun simulateLogin() {
//        isLoggedIn = true; screen = "profile"
//    }
//
//    /** Conferma l'ordine e lo aggiunge allo storico. */
//    fun confirmOrder() {
//        orderId = "#${Random.nextInt(1000, 10000)}"
//        pickupPin = "PIN-${Random.nextInt(100, 1000)}"
//        val summary =
//            if (cart.isNotEmpty()) cart.joinToString(", ") { "${it.quantity}x ${it.product.nameProduct}" } else "Ordine FastBite"
//        val timeStr = "Oggi, " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
//        val payMethodLabel = when (paymentMethod) {
//            "card" -> "💳 Carta di Credito / Google Pay"
//            "paypal" -> "💸 PayPal"
//            else -> "💵 Contanti al ritiro"
//        }
//        val newOrder = OrderHistoryItem(
//            id = orderId,
//            date = timeStr,
//            storeName = selectedStore.name,
//            storeAddress = selectedStore.address,
//            orderType = "Asporto - Ritiro in Cassa ($pickupTime)",
//            paymentMethod = payMethodLabel,
//            itemsSummary = summary,
//            total = cartFinalTotal,
//            status = "In Preparazione",
//            pin = pickupPin
//        )
//        orderHistory.add(0, newOrder)
//        paymentHistory.add(
//            0,
//            PaymentHistoryItem(
//                "TRX-${Random.nextInt(1000, 9999)}",
//                timeStr,
//                cartFinalTotal,
//                payMethodLabel
//            )
//        )
//        screen = "confirmation"
//    }

    /** Resetta lo stato dell'applicazione. */
    fun resetApp() {
        cart.clear(); promoApplied = false; promoInput = ""; screen = "home"
    }
}
