package com.example.foodapp.api

import com.example.foodapp.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interfaccia Retrofit che definisce tutti gli endpoint HTTP (GET e POST)
 * per la comunicazione con il backend PHP/MySQL su XAMPP.
 */
interface FoodApiService {

    /** Recupera la lista completa dei prodotti disponibili nel menu. */
    @GET("index.php")
    suspend fun getProducts(@Query("endpoint") endpoint: String = "products"): List<Product>

    /** Recupera le categorie di prodotti. */
    @GET("index.php")
    suspend fun getCategories(@Query("endpoint") endpoint: String = "categories"): List<Category>

    /** Recupera le promozioni attive. */
    @GET("index.php")
    suspend fun getPromotions(@Query("endpoint") endpoint: String = "promotions"): List<Promotion>

    /** Recupera la lista dei punti vendita (store) per il ritiro. */
    @GET("index.php")
    suspend fun getStores(@Query("endpoint") endpoint: String = "stores"): List<Store>

    /** Recupera i prodotti più popolari / raccomandati. */
    @GET("index.php")
    suspend fun getPopularProducts(@Query("endpoint") endpoint: String = "populars"): List<Product>

    /** Recupera le regole del carrello (es. costi di gestione, sconti). */
    @GET("index.php")
    suspend fun getCartRules(@Query("endpoint") endpoint: String = "cart_rules"): CartRules

    /** Recupera gli slot orari disponibili per il ritiro dell'asporto. */
    @GET("index.php")
    suspend fun getPickupSlots(@Query("endpoint") endpoint: String = "pickup_slots"): PickupSlots

    /** Recupera i metodi di pagamento accettati. */
    @GET("index.php")
    suspend fun getPaymentMethods(@Query("endpoint") endpoint: String = "payment_methods"): List<PaymentMethod>

    /** Recupera le opzioni di personalizzazione e varianti dei prodotti. */
    @GET("index.php")
    suspend fun getProductOptionsResponse(@Query("endpoint") endpoint: String = "options"): List<ProductOptionsResponse>

    /** Esegue una ricerca testuale nel menu dei prodotti. */
    @GET("index.php")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("endpoint") endpoint: String = "search"
    ): List<Product>

    /** Recupera il profilo dell'utente autenticato tramite ID. */
    @GET("index.php")
    suspend fun getUserProfile(
        @Query("user_id") userId: String,
        @Query("endpoint") endpoint: String = "profile"
    ): UserProfile

    /** Recupera lo storico degli ordini effettuati dall'utente. */
    @GET("index.php")
    suspend fun getOrderHistory(
        @Query("user_id") userId: String,
        @Query("endpoint") endpoint: String = "order_history"
    ): List<OrderHistoryItemResponse>

    @GET("index.php")
    suspend fun getPaymentHistory(
        @Query("user_id") userId: String,
        @Query("endpoint") endpoint: String = "payment_history"
    ): List<PaymentHistoryItemResponse>

    /** Invia un nuovo ordine d'asporto al server (POST). */
    @POST("index.php")
    suspend fun createOrder(
        @Query("endpoint") endpoint: String = "create_order",
        @Body orderRequest: PickupOrderRequest
    ): PickupOrderConfirmation

    /** Autentica un utente esistente (Login). */
    @POST("index.php")
    suspend fun loginUser(
        @Query("endpoint") endpoint: String = "login",
        @Body request: AuthRequest
    ): AuthResponse

    /** Registra un nuovo account utente. */
    @POST("index.php")
    suspend fun registerUser(
        @Query("endpoint") endpoint: String = "register",
        @Body request: AuthRequest
    ): AuthResponse
}
