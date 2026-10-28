package com.example.foodapp.data.local

import android.content.Context
import com.example.foodapp.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AssetsJsonReader(private val context: Context) {
    private val gson = Gson()

    private fun readJson(fileName: String): String {
        return context.assets.open("mock/prototipi/$fileName").bufferedReader().use { it.readText() }
    }

    // ottieni tutte le promozioni disponibili
    fun getPromotions(): List<Promotion> {
        val type = object : TypeToken<List<Promotion>>() {}.type
        return gson.fromJson(readJson("promotions.json"), type)
    }

    fun getCategories(): List<Category> {
        val type = object : TypeToken<List<Category>>() {}.type
        return gson.fromJson(readJson("categories.json"), type)
    }

    fun getPopularProducts(): List<Product> {
        val type = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson(readJson("products_popular.json"), type)
    }

    fun getAllProducts(): List<Product> {
        val type = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson(readJson("products_full.json"), type)
    }

    fun getProductById(id: String): Product? =
        getAllProducts().find { it.IdProduct == id }

    fun getProductOptions(productId: String): ProductOptionsResponse {
        return gson.fromJson(
            readJson("product_options_bigmac.json"),
            ProductOptionsResponse::class.java
        )
    }

    fun getStores(): List<Store> {
        val type = object : TypeToken<List<Store>>() {}.type
        return gson.fromJson(readJson("stores.json"), type)
    }

    fun getPickupSlots(): PickupSlots = gson.fromJson(readJson("pickup_slots.json"), PickupSlots::class.java)


    fun getCartRules(): CartRules = gson.fromJson(readJson("cart_summary_rules.json"), CartRules::class.java)

    fun createPickupOrder(req: PickupOrderRequest): PickupOrderConfirmation {
        val store = getStores().find { it.id == req.storeId }
        val pin = (1000..9999).random().toString()
        val time = if (req.pickupType == "immediated") "Subito (circa 15 min)" else req.scheduledTime ?: ""
        return PickupOrderConfirmation(
            orderId = "ORD-${System.currentTimeMillis()}",
            pickupPin = pin,
            pickupTime = time,
            storeName = store?.name ?: "FastBite",
            qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${req.storeId}",
            total = req.total
        )
    }

}