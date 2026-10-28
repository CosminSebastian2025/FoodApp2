package com.example.foodapp.data.model

data class Promotion(
    val id: Int,
    val title: String,
    val subtitle: String,
    val badges: List<String>,
    val ImageUrl: String
)

data class Category(
    val idCategory: String,
    val nameCategory: String,
    val icon: String,
    val count: Int
)

data class Product(
    val IdProduct: String,
    val nameProduct: String,
    val description: String,
    val basePrice: Int,
    val imageUrl: String,
    val category: String,
    val isPopular: Boolean = false,
    val allergens: List<String> = emptyList()
)

data class ProductOptionsResponse(
    val productId: String,
    val groups: List<OptionGroup>
)

data class OptionGroup(
    val groupId: String,
    val groupName: String,
    val isRequired: Boolean,
    val selectionType: String,
    val choises: List<OptionChoise>
)

data class OptionChoise(
    val id: String,
    val name: String,
    val priceModifier: Double
)

data class Store(
    val id: String,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val readyInMinutes: Int,
    val openingHours: String,
    val isPreferred: Boolean
)

data class PickupSlots(
    val immediated: ImmediateSlot,
    val scheduledSlot: ScheduledSlot
)

data class ImmediateSlot ( val label: String, val availableAt: String)
data class ScheduledSlot ( val slotId: String, val time: String, val available: Boolean )

data class CartRules(
    val pickupFee: Double,
    val managementFee: Double,
    val discountCode: String,
    val discountPercent: Int,
    val currency: String
)

// === CART ===

data class CartItem(
    val productId: String,
    val productName: String,
    val basePrice: Double,
    val quantity: Int,
    val selectedOptions: List<String>,
    val optionPriceModifier: Double
){
    val unitPrice: Double get() = basePrice + optionPriceModifier
    val totalPrice: Double get() = unitPrice * quantity
}

// === ORDER ===

data class PickupOrderRequest(
    val userId: String,
    val storeId: String,
    val pickupType: String,
    val scheduledTime: String?,
    val discountCode: String?,
    val items: List<CartItem>,
    val subTotal: Double,
    val discount: Double,
    val managementFee: Double,
    val total: Double
)

data class PickupOrderConfirmation(
    val orderId: String,
    val pickupPin: String,
    val pickupTime: String,
    val storeName: String,
    val qrCodeUrl: String,
    val total: Double
)
