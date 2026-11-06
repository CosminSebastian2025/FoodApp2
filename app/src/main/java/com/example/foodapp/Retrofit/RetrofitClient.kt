package com.example.foodapp.retrofit

import com.example.foodapp.api.FoodApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton client per la gestione delle chiamate di rete tramite Retrofit.
 * Configurato per comunicare con il backend PHP/XAMPP in rete locale.
 */
object RetrofitClient {

    /** URL di base del server API locale (XAMPP). */
    private const val BASE_URL = "http://192.168.2.248/api/"

    /** Istanza lazy del servizio API per eseguire richieste HTTP con Gson (modalità lenient). */
    val apiService: FoodApiService by lazy {
        val gson = GsonBuilder().setLenient().create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FoodApiService::class.java)
    }
}
