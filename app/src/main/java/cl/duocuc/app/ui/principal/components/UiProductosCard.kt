package cl.duocuc.app.ui.principal.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import cl.duocuc.app.model.Producto

@Composable
fun UiProductosCard(
    producto: Producto,
    onAgregar: (Producto) -> Unit,
    onToggleFavorito: () -> Unit
) {
    var agregado by remember { mutableStateOf(false) }
    var esFavorito by remember { mutableStateOf(producto.favorito) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // 🖼 Imagen con badge y botón corazón
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Image(
                    painter = painterResource(producto.imagenRes),
                    contentDescription = producto.nombre,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )

                producto.oferta?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.small,
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // ❤️ Botón favorito
                IconButton(
                    onClick = {
                        esFavorito = !esFavorito
                        onToggleFavorito()  // ✅ se ejecuta sin parámetros
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (esFavorito)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (esFavorito)
                            Color.Red
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

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

            val interactionSource = remember { MutableInteractionSource() }
            val presionado by interactionSource.collectIsPressedAsState()

            val escala by animateFloatAsState(
                targetValue = if (presionado) 0.95f else 1f,
                animationSpec = tween(durationMillis = 100),
                label = "scaleAnim"
            )

            val colorFondo by animateColorAsState(
                targetValue = if (agregado)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.primary,
                animationSpec = tween(durationMillis = 300),
                label = "colorAnim"
            )

            Button(
                onClick = {
                    agregado = !agregado
                    onAgregar(producto)
                },
                interactionSource = interactionSource,
                colors = ButtonDefaults.buttonColors(containerColor = colorFondo),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = escala
                        scaleY = escala
                    }
                    .animateContentSize()
            ) {
                Text(
                    if (agregado) "Agregado ✅" else "Agregar al carrito",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
