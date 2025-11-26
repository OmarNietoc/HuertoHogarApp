package cl.duocuc.app.ui.principal.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duocuc.app.model.Producto

@Composable
fun UiProductosCard(
    producto: Producto,
    onAgregar: (Producto) -> Unit,
    onToggleFavorito: (Producto) -> Unit
) {
    var agregado by remember { mutableStateOf(false) }

    // 🔹 LÓGICA DE DECODIFICACIÓN MANUAL (Plan B infalible)
    val imagenBitmap = remember(producto.imagenBase64) {
        try {
            if (!producto.imagenBase64.isNullOrEmpty()) {
                // 1. Limpiamos la cadena: Quitamos el encabezado "data:image..." si existe y espacios
                val cleanBase64 = producto.imagenBase64
                    .substringAfter(",") // Si tiene prefijo con coma, toma lo que sigue. Si no, toma todo.
                    .trim() // Quita espacios y saltos de línea molestos

                // 2. Decodificamos a Bytes
                val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

                // 3. Convertimos a Bitmap de Compose
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)?.asImageBitmap()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // --- 1. Área de Imagen ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Si logramos decodificar el Base64, mostramos ese Bitmap
                if (imagenBitmap != null) {
                    Image(
                        bitmap = imagenBitmap,
                        contentDescription = producto.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Si no, mostramos la imagen por defecto de los recursos (fallback)
                    Image(
                        painter = painterResource(id = producto.imagenRes),
                        contentDescription = producto.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Etiqueta de Oferta
                producto.oferta?.let { oferta ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.small,
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = oferta,
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Botón Favorito
                IconButton(
                    onClick = { onToggleFavorito(producto) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (producto.favorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (producto.favorito) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // --- 2. Textos ---
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = producto.categoria.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$${producto.precio} / ${producto.unid}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )

            Spacer(Modifier.weight(1f))

            // --- 3. Botón Agregar ---
            val interactionSource = remember { MutableInteractionSource() }
            val presionado by interactionSource.collectIsPressedAsState()
            val escala by animateFloatAsState(if (presionado) 0.95f else 1f, label = "scale")
            val colorFondo by animateColorAsState(
                if (agregado) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                label = "color"
            )

            Button(
                onClick = { onAgregar(producto) },
                interactionSource = interactionSource,
                colors = ButtonDefaults.buttonColors(containerColor = colorFondo),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .graphicsLayer { scaleX = escala; scaleY = escala }
            ) {
                Text("Agregar al carrito", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

// ---------------------------------------------
// Componentes Auxiliares (Carrito y Selector)
// ---------------------------------------------

@Composable
fun ProductoCardCarrito(
    producto: Producto,
    cantidad: Int,
    onCantidadChange: (Int) -> Unit
) {
    // 🔹 Lógica de imagen para el carrito también
    val imagenBitmap = remember(producto.imagenBase64) {
        try {
            if (!producto.imagenBase64.isNullOrEmpty()) {
                val cleanBase64 = producto.imagenBase64.substringAfter(",").trim()
                val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)?.asImageBitmap()
            } else null
        } catch (e: Exception) { null }
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.size(100.dp)
            ) {
                if (imagenBitmap != null) {
                    Image(
                        bitmap = imagenBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = producto.imagenRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), maxLines = 2)
                Text("$${producto.precio} / ${producto.unid}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                CantidadSelector(cantidad = cantidad, onCantidadChange = onCantidadChange)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$${producto.precio * cantidad}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                IconButton(onClick = { onCantidadChange(0) }) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun CantidadSelector(cantidad: Int, onCantidadChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = { onCantidadChange(cantidad - 1) },
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp)
        ) { Text("-", fontWeight = FontWeight.Bold) }

        Text("$cantidad", modifier = Modifier.padding(horizontal = 12.dp))

        Button(
            onClick = { onCantidadChange(cantidad + 1) },
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp)
        ) { Text("+", fontWeight = FontWeight.Bold) }
    }
}