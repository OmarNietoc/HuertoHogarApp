package cl.duocuc.app.data.local.favoritos

import androidx.room.*

@Dao
interface ProductoFavoritoDao {

    @Query("SELECT * FROM producto_favorito")
    suspend fun obtenerTodos(): List<ProductoFavoritoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: ProductoFavoritoEntity)

    @Delete
    suspend fun eliminar(producto: ProductoFavoritoEntity)

    @Query("DELETE FROM producto_favorito WHERE id = :id")
    suspend fun eliminarPorId(id: Int)


    @Query("SELECT EXISTS(SELECT 1 FROM producto_favorito WHERE id = :id)")
    suspend fun esFavorito(id: Int): Boolean
}
