package cl.duocuc.app.data.mappers


import cl.duocuc.app.data.local.productos.ProductoEntity
import cl.duocuc.app.data.local.favoritos.ProductoFavoritoEntity
import cl.duocuc.app.model.Producto

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
