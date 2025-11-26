package cl.duocuc.app.data.network

import cl.duocuc.app.model.Producto
import cl.duocuc.app.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ShoppyApiService {
    @GET("api/products")
    suspend fun getProducts(): List<cl.duocuc.app.data.network.dto.ProductDto>

    @GET("api/users/by-email")
    suspend fun getUserByEmail(@Query("email") email: String): User

    @POST("api/users/add")
    suspend fun addUser(@Body user: cl.duocuc.app.data.network.dto.UserRequestDto): Response<Void>
}
