package cl.duocuc.app.repository.auth

import cl.duocuc.app.model.User
import cl.duocuc.app.data.network.ShoppyApiService // Asegúrate que este import sea correcto
import cl.duocuc.app.data.network.RetrofitClient
import cl.duocuc.app.data.network.dto.UserRequestDto // Asegúrate del import
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

        // Sync with Shoppy
        try {
            apiService.getUserByEmail(email)
        } catch (e: HttpException) {
            // Si es 404 (No encontrado), lo creamos
            if (e.code() == 404) {
                try {
                    val userDto = UserRequestDto(
                        name = "Usuario",
                        email = email,
                        password = pass,
                        role = 2L,
                        status = 1,
                        imagen = null,
                        firebaseId = fu.uid
                    )
                    apiService.addUser(userDto)
                } catch (createError: Exception) {
                    createError.printStackTrace()
                }
            } else {
                e.printStackTrace()
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
            val userDto = UserRequestDto(
                name = "Usuario",
                email = email,
                password = pass,
                role = 2L,
                status = 1,
                imagen = null,
                firebaseId = fu.uid
            )
            apiService.addUser(userDto)
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