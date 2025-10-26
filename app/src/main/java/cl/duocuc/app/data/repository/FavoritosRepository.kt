package cl.duocuc.app.repository.auth

import cl.duocuc.app.data.local.favoritos.ProductoFavoritoDao
import cl.duocuc.app.data.local.favoritos.ProductoFavoritoEntity

class FavoritosRepository(private val dao: ProductoFavoritoDao) {

    suspend fun obtenerTodos() = dao.obtenerTodos()

    suspend fun toggleFavorito(producto: ProductoFavoritoEntity) {
        if (dao.esFavorito(producto.id)) {
            dao.eliminar(producto)
        } else {
            dao.insertar(producto)
        }
    }

    suspend fun esFavorito(id: String) = dao.esFavorito(id)
}
