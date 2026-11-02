package com.example.foodapp.api

import com.example.foodapp.data.model.Product
import retrofit2.http.GET

interface FoodApiService {

    @GET("api/product.php")
    suspend fun getProduct(): List<Product>
}