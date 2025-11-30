package cl.duocuc.app.data.network

import cl.duocuc.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.API_BASE_URL
    @Volatile
    private var authToken: String? = null

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val original = chain.request()
            val token = authToken
            val req = if (!token.isNullOrBlank()) {
                original.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else original
            chain.proceed(req)
        }
        .build()

    val apiService: ShoppyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(ShoppyApiService::class.java)
    }

    fun setAuthToken(token: String?) {
        authToken = token
    }
}
