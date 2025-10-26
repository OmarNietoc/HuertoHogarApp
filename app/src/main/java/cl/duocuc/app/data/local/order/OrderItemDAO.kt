package cl.duocuc.app.data.local.order


import androidx.room.*

@Dao
interface OrderItemDao {

    @Query("SELECT * FROM order_items")
    suspend fun obtenerTodos(): List<OrderItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: OrderItemEntity)

    @Update
    suspend fun actualizar(item: OrderItemEntity)

    @Delete
    suspend fun eliminar(item: OrderItemEntity)

    @Query("DELETE FROM order_items")
    suspend fun limpiarCarrito()

    @Query("SELECT * FROM order_items WHERE productoId = :productoId LIMIT 1")
    suspend fun obtenerPorProductoId(productoId: String): OrderItemEntity?

    @Query("SELECT SUM(cantidad) FROM order_items")
    suspend fun contarProductos(): Int?
}
