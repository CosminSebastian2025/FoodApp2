package com.example.foodapp.data.remote

import com.example.foodapp.data.model.*

interface FastBiteApi {
    suspend fun getPromotions(): List<Promotion>
    suspend fun getCategories(): List<Category>
    suspend fun getPopularProducts(): List<Product>
    suspend fun getAllProducts(): List<Product>
    suspend fun getProductsById(id: String): Product?
    suspend fun getProductOptions(productId: String): ProductOptionsResponse?
    suspend fun getStores(): List<Store>
    suspend fun getPickupSlots(): PickupSlots
    suspend fun getCartRules(): CartRules
    suspend fun createPickupOrder(req: PickupOrderRequest): PickupOrderConfirmation
}