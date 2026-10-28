package com.example.foodapp.data.repository

import com.example.foodapp.data.model.*
import com.example.foodapp.data.remote.FastBiteApi

class FastBiteRepository(private val api: FastBiteApi) {

    suspend fun getPromotions() = api.getPromotions()
    suspend fun getCategories() = api.getCategories()
    suspend fun getPopularProducts() = api.getPopularProducts()
    suspend fun getAllProducts() = api.getAllProducts()
    suspend fun getProductById(id: String) = api.getProductsById(id)
    suspend fun getProductOptions(id: String) = api.getProductOptions(id)
    suspend fun getStores() = api.getStores()
    suspend fun getPickupSlots() = api.getPickupSlots()
    suspend fun getCartRules() = api.getCartRules()
    suspend fun createPickupOrder(req: PickupOrderRequest) = api.createPickupOrder(req)
}