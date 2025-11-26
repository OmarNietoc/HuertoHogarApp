package cl.duocuc.app.repository.auth

import cl.duocuc.app.model.User
import kotlin.String

class AuthRepository(
    private val ds: FirebaseAuthDataSource = FirebaseAuthDataSource()
) {
    private val apiService = cl.duocuc.app.data.network.RetrofitClient.apiService

    suspend fun login(email: String, pass: String): User? {
        val fu = ds.signIn(email, pass) ?: return null
        
        // Sync with Shoppy
        try {
            apiService.getUserByEmail(email)
        } catch (e: retrofit2.HttpException) {
            // If user not found (404), create it
            if (e.code() == 404) {
                try {
                    val userDto = cl.duocuc.app.data.network.dto.UserRequestDto(
                        name = "Usuario",
                        email = email,
                        password = pass,
                        role = 2L,
                        status = 1,
                        imagen=null,
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
            val userDto = cl.duocuc.app.data.network.dto.UserRequestDto(
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

