package com.example.foodapp.data.model

import com.google.gson.annotations.SerializedName

/** Modello dati per le promozioni in evidenza nella home. */
data class Promotion(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("subtitle") val subtitle: String = "",
    @SerializedName("badges") val badges: List<String>? = emptyList(),
    @SerializedName("ImageUrl") val ImageUrl: String = ""
)

/** Modello dati per le categorie di cibo. */
data class Category(
    @SerializedName("idCategory") val idCategory: String = "",
    @SerializedName("nameCategory") val nameCategory: String = "",
    @SerializedName("ImageUrl") val icon: String = "",
    @SerializedName("count") val count: Int = 0
)

/** Modello dati per i prodotti del menu. */
data class Product(
    @SerializedName("IdProduct") val IdProduct: String? = null,
    @SerializedName("nameProduct") val nameProduct: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("basePrice") val basePrice: Double = 0.0,
    @SerializedName("imageUrl") val imageUrl: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("isPopular") val isPopular: Boolean = false,
    @SerializedName("allergens") val allergens: List<String> = emptyList()
) {
    // Proprietà di compatibilità per la UI
    val id: String get() = IdProduct ?: ""
    val name: String get() = nameProduct
    val price: Double get() = basePrice
    val emoji: String get() = imageUrl

    // Costruttore secondario per compatibilità con il codice UI locale
    constructor(
        id: String,
        category: String,
        name: String,
        description: String,
        price: Double,
        emoji: String,
        isPopular: Boolean = false
    ) : this(
        IdProduct = id,
        nameProduct = name,
        description = description,
        basePrice = price,
        imageUrl = emoji,
        category = category,
        isPopular = isPopular,
        allergens = emptyList()
    )
}

/** Risposta contenente le opzioni di personalizzazione di un prodotto. */
data class ProductOptionsResponse(
    @SerializedName("productId") val productId: String,
    @SerializedName("groups") val groups: List<OptionGroup>
)

/** Gruppo di opzioni di personalizzazione (es. Salse, Ingredienti). */
data class OptionGroup(
    @SerializedName("groupId") val groupId: String,
    @SerializedName("groupName") val groupName: String,
    @SerializedName("isRequired") val isRequired: Boolean,
    @SerializedName("selectionType") val selectionType: String,
    @SerializedName("choises") val choises: List<OptionChoise>
)

/** Singola scelta all'interno di un gruppo di opzioni. */
data class OptionChoise(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("priceModifier") val priceModifier: Double
)

/** Modello dati per i punti vendita FastBite. */
data class Store(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("distanceKm") val distanceKm: Double,
    @SerializedName("readyInMinutes") val readyInMinutes: Int,
    @SerializedName("openingHours") val openingHours: String,
    @SerializedName("isPreferred") val isPreferred: Boolean
)

/** Slot orari disponibili per il ritiro. */
data class PickupSlots(
    @SerializedName("immediated") val immediated: ImmediateSlot,
    @SerializedName("scheduled") val scheduled: List<ScheduledSlot>
)

data class ImmediateSlot(
    @SerializedName("label") val label: String,
    @SerializedName("availableAt") val availableAt: String? = null
)

data class ScheduledSlot(
    @SerializedName("slotId") val slotId: String,
    @SerializedName("time") val time: String,
    @SerializedName("available") val available: Boolean
)

/** Metodo di pagamento accettato. */
data class PaymentMethod(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("label") val label: String,
    @SerializedName("isDefault") val isDefault: Boolean
)

/** Regole e tariffe del carrello. */
data class CartRules(
    @SerializedName("pickupFee") val pickupFee: Double,
    @SerializedName("managementFee") val managementFee: Double,
    @SerializedName("discountCode") val discountCode: String,
    @SerializedName("discountPercent") val discountPercent: Int,
    @SerializedName("currency") val currency: String
)

// === CART ===

/** Articolo inserito nel carrello con opzioni e quantità. */
data class CartItem(
    @SerializedName("productId") val productId: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("selectedOptions") val selectedOptions: List<String>,
    @SerializedName("optionPriceModifier") val optionPriceModifier: Double
) {
    val unitPrice: Double get() = basePrice + optionPriceModifier
    val totalPrice: Double get() = unitPrice * quantity
}

// === ORDER ===

/** Richiesta POST per la creazione di un nuovo ordine. */
data class PickupOrderRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("storeId") val storeId: String,
    @SerializedName("pickupType") val pickupType: String,
    @SerializedName("scheduledTime") val scheduledTime: String?,
    @SerializedName("discountCode") val discountCode: String?,
    @SerializedName("items") val items: List<CartItem>,
    @SerializedName("subTotal") val subTotal: Double,
    @SerializedName("discount") val discount: Double,
    @SerializedName("managementFee") val managementFee: Double,
    @SerializedName("total") val total: Double,
    @SerializedName("paymentMethod") val paymentMethod: String
)

/** Conferma dell'ordine ricevuto dal server con PIN di ritiro e QR code. */
data class PickupOrderConfirmation(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("pickupPin") val pickupPin: String,
    @SerializedName("pickupTime") val pickupTime: String,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("qrCodeUrl") val qrCodeUrl: String,
    @SerializedName("total") val total: Double
)

/** Auth */
data class AuthRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String? = null
)

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("userId") val userId: String,
    @SerializedName("message") val message: String
)

data class UserProfile(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String
)

data class OrderHistoryItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("items_summary") val itemsSummary: String,
    @SerializedName("total") val total: Double,
    @SerializedName("storeAddress") val storeAddress: String,
    @SerializedName("order_type") val orderType: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("status") val status: String,
    @SerializedName("pin") val pin: String
)

data class PaymentHistoryItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("status") val status: String
)
