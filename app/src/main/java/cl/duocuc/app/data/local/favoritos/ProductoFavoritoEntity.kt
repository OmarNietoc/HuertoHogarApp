package cl.duocuc.app.data.local.favoritos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto_favorito")
data class ProductoFavoritoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val unid: String,
    val imagenRes: Int,
    val oferta: String? = null
)
