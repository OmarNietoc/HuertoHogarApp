package cl.duocuc.app.ui.home.components


import android.R.attr.rotation
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.animation.OvershootInterpolator
import cl.duocuc.app.R

@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier,
    logoRes: Int = R.drawable.logo,
    contentScale: ContentScale = ContentScale.Fit
) {
    var rotated by remember { mutableStateOf(false) }
    var appeared by remember { mutableStateOf(false) }

    // Entrada con overshoot
    val enterScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(
            durationMillis = 900,
            easing = {
                OvershootInterpolator(2f).getInterpolation(it)
            }
        ),
        label = "enterScale"
    )

    val enterAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(900),
        label = "enterAlpha"
    )

    // Pulso y rotación infinitos
    val infinite = rememberInfiniteTransition(label = "logoInfinite")

    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val tilt by infinite.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )

    // Animación de rotación
    val rotation by animateFloatAsState(
        targetValue = if (rotated) 360f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        finishedListener = {
            rotated = false
        }
    )

    LaunchedEffect(Unit) { appeared = true }

    Image(
        painter = painterResource(id = logoRes),
        contentDescription = "Logo animado",
        contentScale = contentScale,
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .graphicsLayer {
                scaleX = enterScale * pulse
                scaleY = enterScale * pulse
                rotationZ = tilt
                alpha = enterAlpha
                shadowElevation = 0f
            }
            .rotate(rotation.toFloat())
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // elimina ripple y silueta
            ) { rotated = true },
    )
}
