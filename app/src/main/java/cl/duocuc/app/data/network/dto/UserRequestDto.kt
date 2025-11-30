package cl.duocuc.app.data.network.dto

data class UserRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val role: Long = 2L,
    val status: Int = 1,
    val imagen: String? = null,
    val firebaseId: String? = null,
    val phone: String? = null,
    val region: Long? = null,
    val comuna: Long? = null
)
