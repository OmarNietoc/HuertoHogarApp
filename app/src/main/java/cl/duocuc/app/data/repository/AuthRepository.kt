package cl.duocuc.app.repository.auth

import cl.duocuc.app.model.User
import cl.duocuc.app.data.network.ShoppyApiService
import cl.duocuc.app.data.network.RetrofitClient
import cl.duocuc.app.data.network.dto.LoginRequest
import cl.duocuc.app.data.network.dto.RegisterRequest
import cl.duocuc.app.data.network.dto.UserRequestDto
import retrofit2.HttpException
import kotlin.String

class AuthRepository(
    private val ds: FirebaseAuthDataSource = FirebaseAuthDataSource(),
    // CAMBIO CLAVE: Ahora la API entra por el constructor
    private val apiService: ShoppyApiService = RetrofitClient.apiService
) {
    // Ya no creamos la variable aquí adentro, usamos la que viene del constructor

    suspend fun login(email: String, pass: String): User? {
        val fu = ds.signIn(email, pass) ?: return null

        // Login en Shoppy para obtener token y compatibilidad con endpoints protegidos
        try {
            val loginResp = apiService.login(LoginRequest(email = email, password = pass))
            RetrofitClient.setAuthToken(loginResp.token)

            // Verifica existencia; si no existe, registra con rol USER
            val userResp = apiService.getUserByEmail(email)
            if (!userResp.isSuccessful) {
                apiService.register(
                    RegisterRequest(
                        name = fu.displayName ?: "Usuario",
                        email = email,
                        password = pass,
                        role = 2L,
                        status = 1,
                        firebaseId = fu.uid
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return User(uid = fu.uid, email = fu.email)
    }

    suspend fun signUp(email: String, pass: String): User? {
        print("Comienza login")
        val fu = ds.signUp(email, pass) ?: return null

        // Sync with Shoppy
        try {
            apiService.register(
                RegisterRequest(
                    name = fu.displayName ?: "Usuario",
                    email = email,
                    password = pass,
                    role = 2L,
                    status = 1,
                    firebaseId = fu.uid
                )
            )
            // Login inmediato para obtener token
            val loginResp = apiService.login(LoginRequest(email = email, password = pass))
            RetrofitClient.setAuthToken(loginResp.token)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return User(uid = fu.uid, email = fu.email)
    }

    suspend fun sendPasswordReset(email: String): Boolean {
        return ds.sendPasswordReset(email)
    }

    fun logout() = ds.signOut()
    fun currentUser(): User? = ds.currentUser()?.let { User(it.uid, it.email) }
}
