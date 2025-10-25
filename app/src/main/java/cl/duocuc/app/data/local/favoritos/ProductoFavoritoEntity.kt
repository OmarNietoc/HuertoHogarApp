package cl.duocuc.app.data.local.favoritos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto_favorito")
data class ProductoFavoritoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val unid: String,
    val imagenRes: Int,
    val oferta: String? = null
)
