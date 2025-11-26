package cl.duocuc.app.data.network.dto

data class ProductDto(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val stock: Int,
    val stockMinimo: Int,
    val activo: Int,
    val categoria: CategoryDto?,
    val unid: UnitDto?,
    val imagen: String? // Base64
)

data class CategoryDto(
    val id: Long,
    val name: String
)

data class UnitDto(
    val id: Long,
    val name: String
)
