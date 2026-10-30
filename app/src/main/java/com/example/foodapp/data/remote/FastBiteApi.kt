package com.example.foodapp.data.remote

import com.example.foodapp.data.model.CartRules
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.PaymentMethod
import com.example.foodapp.data.model.PickupOrderConfirmation
import com.example.foodapp.data.model.PickupOrderRequest
import com.example.foodapp.data.model.PickupSlots
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.ProductOptionsResponse
import com.example.foodapp.data.model.Promotion
import com.example.foodapp.data.model.Store

interface FastBiteApi {
    suspend fun getPromotions(): List<Promotion>
    suspend fun getCategories(): List<Category>
    suspend fun getPopularProducts(): List<Product>
    suspend fun getAllProducts(): List<Product>
    suspend fun getProductsById(id: String): Product?
    suspend fun getProductOptions(productId: String): ProductOptionsResponse?
    suspend fun getStores(): List<Store>
    suspend fun getPickupSlots(): PickupSlots
    suspend fun getPaymentMethods(): List<PaymentMethod>
    suspend fun getCartRules(): CartRules
    suspend fun createPickupOrder(req: PickupOrderRequest): PickupOrderConfirmation
}