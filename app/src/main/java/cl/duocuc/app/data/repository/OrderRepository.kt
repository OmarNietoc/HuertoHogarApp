package cl.duocuc.app.data.repository


import cl.duocuc.app.data.local.AppDatabase
import cl.duocuc.app.data.local.order.OrderItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import cl.duocuc.app.data.mappers.toModel


class OrderRepository(private val db: AppDatabase) {

    private val orderItemDao = db.orderItemDao()


    suspend fun obtenerCarrito(): List<OrderItemEntity> = withContext(Dispatchers.IO) {
        orderItemDao.obtenerTodos()
    }

    suspend fun agregarAlCarrito(
        productoId: String,
        nombre: String,
        precioUnitario: Int,
        cantidad: Int
    ) = withContext(Dispatchers.IO) {
        val subtotal = precioUnitario * cantidad

        val existente = orderItemDao.obtenerPorProductoId(productoId)
        if (existente != null) {
            // Si ya existe, actualiza la cantidad
            val nuevo = existente.copy(
                cantidad = existente.cantidad + cantidad,
                subtotal = (existente.cantidad + cantidad) * precioUnitario
            )
            orderItemDao.actualizar(nuevo)
        } else {
            // Si no existe, inserta nuevo
            orderItemDao.insertar(
                OrderItemEntity(
                    productoId = productoId,
                    nombre = nombre,
                    precioUnitario = precioUnitario,
                    cantidad = cantidad,
                    subtotal = subtotal
                )
            )
        }
    }

    suspend fun eliminarDelCarrito(productoId: String) = withContext(Dispatchers.IO) {
        orderItemDao.obtenerPorProductoId(productoId)?.let {
            orderItemDao.eliminar(it)
        }
    }

    suspend fun limpiarCarrito() = withContext(Dispatchers.IO) {
        orderItemDao.limpiarCarrito()
    }

    suspend fun calcularTotal(): Int = withContext(Dispatchers.IO) {
        orderItemDao.obtenerTodos().sumOf { it.subtotal }
    }

    suspend fun contarProductos(): Int = withContext(Dispatchers.IO) {
        orderItemDao.contarProductos() ?: 0
    }

    suspend fun actualizarCantidadCarrito(productoId: String, nuevaCantidad: Int) = withContext(Dispatchers.IO) {
        val existente = orderItemDao.obtenerPorProductoId(productoId)
        if (existente != null) {
            if (nuevaCantidad > 0) {
                // Actualizar cantidad y subtotal
                val actualizado = existente.copy(
                    cantidad = nuevaCantidad,
                    subtotal = nuevaCantidad * existente.precioUnitario
                )
                orderItemDao.actualizar(actualizado)
            } else {
                // Si la cantidad es 0 o menor, eliminar del carrito
                orderItemDao.eliminar(existente)
            }
        }
    }
}
