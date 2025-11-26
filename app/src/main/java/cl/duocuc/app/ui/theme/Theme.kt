package cl.duocuc.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



// ==================== Paletas de color ====================
private val LightColors = lightColorScheme(
    primary = VerdeEsmeralda,
    onPrimary = Color.White,
    secondary = AmarilloMostaza,
    onSecondary = GrisOscuro,
    background = FondoSuave,
    onBackground = GrisOscuro,
    surface = Color.White,
    onSurface = GrisOscuro
)

private val DarkColors = darkColorScheme(
    primary = VerdeEsmeralda,
    onPrimary = Color.White,
    secondary = AmarilloMostaza,
    onSecondary = GrisOscuro,
    background = FondoSuave,
    onBackground = GrisOscuro,
    surface = Color.White,
    onSurface = GrisOscuro
)

// ==================== Tema principal ====================
@Composable
fun HuertoHogarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Force LightColors regardless of darkTheme setting
    val colors = LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
