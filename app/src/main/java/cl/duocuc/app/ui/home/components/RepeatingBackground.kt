package cl.duocuc.app.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource

import cl.duocuc.app.R

@Composable
fun RepeatingBackground(
    imageRes: Int = R.drawable.textura_papel,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Cargar la imagen desde recursos
    val img: ImageBitmap = ImageBitmap.imageResource(R.drawable.textura_papel)

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val imgWidth = img.width.toFloat()
            val imgHeight = img.height.toFloat()
            val xRepeat = (size.width / imgWidth).toInt() + 1
            val yRepeat = (size.height / imgHeight).toInt() + 1

            for (y in 0 until yRepeat) {
                for (x in 0 until xRepeat) {
                    drawImage(img, topLeft = Offset(x * imgWidth, y * imgHeight))
                }
            }
        }

        // Contenido encima del fondo
        content()
    }
}
