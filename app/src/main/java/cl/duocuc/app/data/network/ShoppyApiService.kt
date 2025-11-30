package cl.duocuc.app.data.network

import cl.duocuc.app.data.network.dto.*
import cl.duocuc.app.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ShoppyApiService {
    @GET("api/products")
    suspend fun getProducts(): List<cl.duocuc.app.data.network.dto.ProductDto>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<Void>

    @GET("api/users/by-email")
    suspend fun getUserByEmail(@Query("email") email: String): Response<User>
}
