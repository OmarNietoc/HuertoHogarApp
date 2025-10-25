package cl.duocuc.app.repository.auth

import cl.duocuc.app.model.User

class AuthRepository(
    private val ds: FirebaseAuthDataSource = FirebaseAuthDataSource()
) {
    suspend fun login(email: String, pass: String): User? {
        val fu = ds.signIn(email, pass) ?: return null
        return User(uid = fu.uid, email = fu.email)
    }

    suspend fun signUp(email: String, pass: String): User? {
        print("Comienza login")
        val fu = ds.signUp(email, pass) ?: return null
        return User(uid = fu.uid, email = fu.email)
    }

    suspend fun sendPasswordReset(email: String): Boolean {
        return ds.sendPasswordReset(email)
    }

    fun logout() = ds.signOut()
    fun currentUser(): User? = ds.currentUser()?.let { User(it.uid, it.email) }
}

