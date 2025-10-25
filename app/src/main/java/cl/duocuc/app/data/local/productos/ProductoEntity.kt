package cl.duocuc.app.data.local.productos

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto")
data class ProductoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val categoria: String,
    @DrawableRes val imagenRes: Int,
    val unid: String,
    val oferta: String? = null,
    val favorito: Boolean = false
)
