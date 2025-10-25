package cl.duocuc.app.data.repository

import cl.duocuc.app.data.local.AppDatabase
import cl.duocuc.app.data.local.favoritos.ProductoFavoritoEntity
import cl.duocuc.app.data.local.productos.ProductoEntity
import cl.duocuc.app.data.mappers.toModel
import cl.duocuc.app.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductoRepository(private val db: AppDatabase) {

    private val productoDao = db.productoDao()
    private val productoFavoritoDao = db.productoFavoritoDao()

    suspend fun obtenerProductos(): List<Producto> = withContext(Dispatchers.IO) {
        productoDao.obtenerTodos().map { it.toModel() }
    }

    suspend fun toggleFavorito(producto: Producto) {
        withContext(Dispatchers.IO) {
            productoDao.actualizarFavorito(producto.id, producto.favorito)

            if (producto.favorito) {
                productoFavoritoDao.insertar(
                    ProductoFavoritoEntity(
                        id = producto.id.hashCode(),
                        nombre = producto.nombre,
                        categoria = producto.categoria,
                        precio = producto.precio.toDouble(),
                        unid = producto.unid,
                        imagenRes = producto.imagenRes,
                        oferta = producto.oferta
                    )
                )
            } else {
                productoFavoritoDao.eliminarPorId(producto.id.hashCode())
            }
        }
    }

    suspend fun obtenerFavoritos(): List<Producto> = withContext(Dispatchers.IO) {
        productoFavoritoDao.obtenerTodos().map { it.toModel() }
    }
}
