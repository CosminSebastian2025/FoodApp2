package com.example.foodapp.api

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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FoodApiService {

    @GET("index.php")
    suspend fun getProducts(@Query("endpoint") endpoint: String = "products"): List<Product>

    @GET("index.php")
    suspend fun getCategories(@Query("endpoint") endpoint: String = "categories"): List<Category>

    @GET("index.php")
    suspend fun getPromotions(@Query("endpoint") endpoint: String = "promotions"): List<Promotion>

    @GET("index.php")
    suspend fun getStores(@Query("endpoint") endpoint: String = "stores"): List<Store>

    @GET("index.php")
    suspend fun getPopularProducts(@Query("endpoint") endpoint: String = "populars"): List<Product>

    @GET("index.php")
    suspend fun getCartRules(@Query("endpoint") endpoint: String = "cart_rules"): CartRules

    @GET("index.php")
    suspend fun getPickupSlots(@Query("endpoint") endpoint: String = "pickup_slots"): PickupSlots

    @GET("index.php")
    suspend fun getPaymentMethods(@Query("endpoint") endpoint: String = "payment_methods"): List<PaymentMethod>

    @GET("index.php")
    suspend fun getProductOptionsResponse(@Query("endpoint") endpoint: String = "options"): List<ProductOptionsResponse>


    @POST("index.php")
    suspend fun createOrder(
        @Query("endpoint") endpoint: String = "create_order",
        @Body orderRequest: PickupOrderRequest
    ): PickupOrderConfirmation
}