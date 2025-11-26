package cl.duocuc.app.data.repository

import cl.duocuc.app.data.local.AppDatabase
import cl.duocuc.app.data.local.favoritos.ProductoFavoritoEntity
import cl.duocuc.app.data.mappers.toModel
import cl.duocuc.app.data.network.RetrofitClient
import cl.duocuc.app.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.map

class ProductoRepository(private val db: AppDatabase) {

    private val productoDao = db.productoDao()
    private val productoFavoritoDao = db.productoFavoritoDao()
    private val apiService = RetrofitClient.apiService

    suspend fun obtenerProductos(): List<Producto> = withContext(Dispatchers.IO) {
        try {
            val remoteProducts = apiService.getProducts().map { it.toModel() }
            val favoritosIds = productoFavoritoDao.obtenerTodos().map { it.id.toString() }
            
            remoteProducts.map { 
                if (favoritosIds.contains(it.id)) {
                    it.copy(favorito = true)
                } else {
                    it
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun toggleFavorito(producto: Producto) {
        withContext(Dispatchers.IO) {
            productoDao.actualizarFavorito(producto.id, producto.favorito)

            if (producto.favorito) {
                productoFavoritoDao.insertar(
                    ProductoFavoritoEntity(
                        id = producto.id,
                        nombre = producto.nombre,
                        categoria = producto.categoria,
                        precio = producto.precio,
                        unid = producto.unid,
                        imagenRes = producto.imagenRes,
                        oferta = producto.oferta
                    )
                )
            } else {
                productoFavoritoDao.eliminarPorId(producto.id)
            }
        }
    }

    suspend fun obtenerFavoritos(): List<Producto> = withContext(Dispatchers.IO) {
        productoFavoritoDao.obtenerTodos().map { it.toModel() }
    }
}
