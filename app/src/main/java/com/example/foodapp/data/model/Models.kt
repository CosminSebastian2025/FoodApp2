package com.example.foodapp.data.model

import com.google.gson.annotations.SerializedName

/** Modello dati per le promozioni in evidenza nella home. */
data class Promotion(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("badges") val badges: List<String>,
    @SerializedName("image_url") val ImageUrl: String
)

/** Modello dati per le categorie di cibo. */
data class Category(
    @SerializedName("id_category") val idCategory: String,
    @SerializedName("name_category") val nameCategory: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("count") val count: Int
)

/** Modello dati per i prodotti del menu. */
data class Product(
    @SerializedName("id_product") val IdProduct: String? = null,
    @SerializedName("name_product") val nameProduct: String,
    @SerializedName("description") val description: String,
    @SerializedName("base_price") val basePrice: Double,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("category") val category: String,
    @SerializedName("is_popular") val isPopular: Boolean = false,
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
        isPopular = isPopular
    )
}

/** Risposta contenente le opzioni di personalizzazione di un prodotto. */
data class ProductOptionsResponse(
    @SerializedName("product_id") val productId: String,
    @SerializedName("groups") val groups: List<OptionGroup>
)

/** Gruppo di opzioni di personalizzazione (es. Salse, Ingredienti). */
data class OptionGroup(
    @SerializedName("group_id") val groupId: String,
    @SerializedName("group_name") val groupName: String,
    @SerializedName("is_required") val isRequired: Boolean,
    @SerializedName("selection_type") val selectionType: String,
    @SerializedName("choises") val choises: List<OptionChoise>
)

/** Singola scelta all'interno di un gruppo di opzioni. */
data class OptionChoise(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("price_modifier") val priceModifier: Double
)

/** Modello dati per i punti vendita FastBite. */
data class Store(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("distance_km") val distanceKm: Double,
    @SerializedName("ready_in_minutes") val readyInMinutes: Int,
    @SerializedName("opening_hours") val openingHours: String,
    @SerializedName("is_preferred") val isPreferred: Boolean
)

/** Slot orari disponibili per il ritiro. */
data class PickupSlots(
    @SerializedName("immediated") val immediated: ImmediateSlot,
    @SerializedName("scheduled") val scheduled: List<ScheduledSlot>
)

data class ImmediateSlot(
    @SerializedName("label") val label: String,
    @SerializedName("available_at") val availableAt: String
)

data class ScheduledSlot(
    @SerializedName("slot_id") val slotId: String,
    @SerializedName("time") val time: String,
    @SerializedName("available") val available: Boolean
)

/** Metodo di pagamento accettato. */
data class PaymentMethod(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("label") val label: String,
    @SerializedName("is_default") val isDefault: Boolean
)

/** Regole e tariffe del carrello. */
data class CartRules(
    @SerializedName("pickup_fee") val pickupFee: Double,
    @SerializedName("management_fee") val managementFee: Double,
    @SerializedName("discount_code") val discountCode: String,
    @SerializedName("discount_percent") val discountPercent: Int,
    @SerializedName("currency") val currency: String
)

// === CART ===

/** Articolo inserito nel carrello con opzioni e quantità. */
data class CartItem(
    @SerializedName("product_id") val productId: String,
    @SerializedName("product_name") val productName: String,
    @SerializedName("base_price") val basePrice: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("selected_options") val selectedOptions: List<String>,
    @SerializedName("option_price_modifier") val optionPriceModifier: Double
) {
    val unitPrice: Double get() = basePrice + optionPriceModifier
    val totalPrice: Double get() = unitPrice * quantity
}

// === ORDER ===

/** Richiesta POST per la creazione di un nuovo ordine. */
data class PickupOrderRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("store_id") val storeId: String,
    @SerializedName("pickup_type") val pickupType: String,
    @SerializedName("scheduled_time") val scheduledTime: String?,
    @SerializedName("discount_code") val discountCode: String?,
    @SerializedName("items") val items: List<CartItem>,
    @SerializedName("sub_total") val subTotal: Double,
    @SerializedName("discount") val discount: Double,
    @SerializedName("management_fee") val managementFee: Double,
    @SerializedName("total") val total: Double
)

/** Conferma dell'ordine ricevuto dal server con PIN di ritiro e QR code. */
data class PickupOrderConfirmation(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("pickup_pin") val pickupPin: String,
    @SerializedName("pickup_time") val pickupTime: String,
    @SerializedName("store_name") val storeName: String,
    @SerializedName("qr_code_url") val qrCodeUrl: String,
    @SerializedName("total") val total: Double
)

/** Auth */
data class AuthRequest(
    @SerializedName("email") val email: String,
    @SerializedName("pass") val pass: String
)

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("userId") val userId: String,
    @SerializedName("message") val message: String
)

data class UserProfile(
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String
)

data class OrderHistoryItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("store_name") val storeName: String,
    @SerializedName("items_summary") val itemsSummary: String,
    @SerializedName("total") val total: Double,
    @SerializedName("store_address") val storeAddress: String,
    @SerializedName("order_type") val orderType: String,
    @SerializedName("payment_method") val paymentMethod: String,
    val status: String,
    val pin: String
)

data class PaymentHistoryItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("status") val status: String
)
