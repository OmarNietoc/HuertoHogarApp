package cl.duocuc.app.data.local.productos

import androidx.room.*

@Dao
interface ProductoDao {

    @Query("SELECT * FROM producto")
    suspend fun obtenerTodos(): List<ProductoEntity>

    @Query("SELECT * FROM producto WHERE id = :id")
    suspend fun obtenerPorId(id: String): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<ProductoEntity>)

    @Update
    suspend fun actualizar(producto: ProductoEntity)

    @Query("UPDATE producto SET favorito = :favorito WHERE id = :id")
    suspend fun actualizarFavorito(id: String, favorito: Boolean)

    @Query("DELETE FROM producto")
    suspend fun eliminarTodos()
}
