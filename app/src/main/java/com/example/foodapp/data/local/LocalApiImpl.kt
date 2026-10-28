package com.example.foodapp.data.local

import com.example.foodapp.data.model.CartRules
import com.example.foodapp.data.model.PickupOrderConfirmation
import com.example.foodapp.data.model.PickupOrderRequest
import com.example.foodapp.data.model.PickupSlots
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.ProductOptionsResponse
import com.example.foodapp.data.model.Store
import com.example.foodapp.data.remote.FastBiteApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class LocalApiImpl(private val reader: AssetsJsonReader): FastBiteApi {

    override suspend fun getPromotions() = withContext(Dispatchers.IO) {
        delay(200)  //simula latenza di rete
        reader.getPromotions()
    }

    override suspend fun getCategories() = withContext(Dispatchers.IO) {
        delay(150)  //simula latenza di rete
        reader.getCategories()
    }

    override suspend fun getPopularProducts() = withContext(Dispatchers.IO) {
        delay(300)  //simula latenza di rete
        reader.getPopularProducts()
    }

    override suspend fun getAllProducts() = withContext(Dispatchers.IO) {
        delay(250)  //simula latenza di rete
        reader.getAllProducts()
    }

    override suspend fun getProductsById(id: String) = withContext(Dispatchers.IO) {
        reader.getProductById(id)
    }

    override suspend fun getProductOptions(productId: String) = withContext(Dispatchers.IO) {
        reader.getProductOptions(productId)
    }

    override suspend fun getStores() = withContext(Dispatchers.IO) {
        reader.getStores()
    }

    override suspend fun getPickupSlots() = withContext(Dispatchers.IO) {
        reader.getPickupSlots()
    }

    override suspend fun getCartRules() = withContext(Dispatchers.IO) {
        reader.getCartRules()
    }

    override suspend fun createPickupOrder(req: PickupOrderRequest) = withContext(Dispatchers.IO) {
        delay(500)  //simula latenza di rete
        reader.createPickupOrder(req)
    }
}