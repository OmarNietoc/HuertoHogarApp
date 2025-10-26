package cl.duocuc.app.data.local.order


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: String,
    val nombre: String,
    val precioUnitario: Int,
    val cantidad: Int,
    val subtotal: Int
)
