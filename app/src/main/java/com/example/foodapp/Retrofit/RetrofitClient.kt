package com.example.foodapp.retrofit

import com.example.foodapp.api.FoodApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton client per la gestione delle chiamate di rete tramite Retrofit.
 * Configurato per comunicare con il backend PHP/XAMPP in rete locale.
 */
object RetrofitClient {

    /** URL di base del server API locale (XAMPP). */
    private const val BASE_URL = "http://192.168.2.248/api/"

    /** Timeout ridotto: se il server non c'è, l'errore arriva subito all'utente. */
    private const val TIMEOUT_SECONDS = 30L

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    /** Istanza lazy del servizio API per eseguire richieste HTTP con Gson (modalità lenient). */
    val apiService: FoodApiService by lazy {
        val gson = GsonBuilder().setLenient().create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FoodApiService::class.java)
    }

    /**
     * Converte il campo imageUrl del prodotto in un URL assoluto.
     * Restituisce null quando il valore non è un URL (es. emoji).
     */
    fun resolveImageUrl(raw: String): String? {
        val value = raw.trim()
        return when {
            value.isEmpty() -> null
            value.startsWith("http://", true) || value.startsWith("https://", true) -> value
            value.startsWith("/") -> BASE_URL.trimEnd('/').removeSuffix("/api") + value
            // Percorsi relativi (es. images/prod_1.png) vs emoji/testo semplice.
            value.contains("/") || value.substringAfterLast('.', "").lowercase() in
                    setOf("png", "jpg", "jpeg", "webp", "gif", "avif", "svg") -> BASE_URL + value

            else -> null
        }
    }
}
