package cl.duocuc.app.data.network.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: Long = 2L,
    val status: Int = 1,
    val phone: String? = null,
    val region: Long? = null,
    val comuna: Long? = null,
    val firebaseId: String? = null
)
