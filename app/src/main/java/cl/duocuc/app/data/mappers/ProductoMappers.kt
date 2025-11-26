package cl.duocuc.app.data.mappers


import cl.duocuc.app.data.local.productos.ProductoEntity
import cl.duocuc.app.data.local.favoritos.ProductoFavoritoEntity
import cl.duocuc.app.model.Producto
import cl.duocuc.app.data.network.dto.ProductDto
import cl.duocuc.app.R

fun ProductoEntity.toModel() = Producto(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    categoria = categoria,
    imagenRes = imagenRes,
    unid = unid,
    oferta = oferta,
    favorito = favorito
)

fun ProductoFavoritoEntity.toModel() = Producto(
    id = id.toString(),
    nombre = nombre,
    descripcion = "",
    precio = precio.toInt(),
    categoria = categoria,
    imagenRes = imagenRes,
    unid = unid,
    oferta = oferta,
    favorito = true
)

fun ProductDto.toModel() = Producto(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    categoria = categoria?.name ?: "Sin categoría",
    // Si hay imagen y no es una URL (http), asumimos que es Base64 y le agregamos el prefijo.
    imagenBase64 = if (!imagen.isNullOrEmpty() && !imagen.startsWith("http") && !imagen.startsWith("data:")) {
        "data:image/jpeg;base64,$imagen"
    } else {
        imagen
    },
    unid = unid?.name ?: "Unidad",
    oferta = null,
    favorito = false
)
