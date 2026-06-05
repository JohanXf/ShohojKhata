package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
    val access_token: String? = null,
    val token_type: String? = null,
    val expires_in: Long? = null,
    val user: SupabaseAuthUser? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthUser(
    val id: String? = null,
    val email: String? = null,
    val phone: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthRequest(
    val email: String,
    val password: String
)

interface SupabaseApi {
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Body request: SupabaseAuthRequest
    ): SupabaseAuthResponse

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Body request: SupabaseAuthRequest
    ): SupabaseAuthResponse

    // Profiles REST CRUD
    @GET("rest/v1/users")
    suspend fun getUsers(
        @Query("phone") phoneFilter: String? = null
    ): List<User>

    @POST("rest/v1/users")
    suspend fun upsertUser(
        @Body user: User,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates"
    ): List<User>

    // Customers REST CRUD
    @GET("rest/v1/customers")
    suspend fun getCustomers(
        @Query("ownerId") ownerIdFilter: String? = null
    ): List<Customer>

    @POST("rest/v1/customers")
    suspend fun upsertCustomers(
        @Body customers: List<Customer>,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates"
    )

    @DELETE("rest/v1/customers")
    suspend fun deleteCustomer(
        @Query("id") idFilter: String
    )

    // Transactions REST CRUD
    @GET("rest/v1/transactions")
    suspend fun getTransactions(): List<Transaction>

    @POST("rest/v1/transactions")
    suspend fun upsertTransactions(
        @Body transactions: List<Transaction>,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates"
    )

    @DELETE("rest/v1/transactions")
    suspend fun deleteTransaction(
        @Query("id") idFilter: String
    )
}

object SupabaseClient {
    fun isConfigured(): Boolean {
        // Safe check for build configuration constants
        val url = runCatching { BuildConfig.SUPABASE_URL }.getOrNull() ?: ""
        val key = runCatching { BuildConfig.SUPABASE_ANON_KEY }.getOrNull() ?: ""
        return url.isNotBlank() && 
               !url.contains("your-project-id") && 
               key.isNotBlank() && 
               !key.contains("your-anon-public-api-key")
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    val api: SupabaseApi? by lazy {
        if (!isConfigured()) return@lazy null

        val baseUrl = BuildConfig.SUPABASE_URL.trim().let {
            if (it.endsWith("/")) it else "$it/"
        }
        val anonKey = BuildConfig.SUPABASE_ANON_KEY.trim()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("apikey", anonKey)
                    .addHeader("Authorization", "Bearer $anonKey")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        try {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(SupabaseApi::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
