package cl.duocuc.app.ui.theme

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



@Composable
fun customTextFieldColors(darkTheme: Boolean): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedContainerColor = if (darkTheme) GrisMedio else Color.White,
        unfocusedContainerColor = if (darkTheme) GrisMedio else Color.White,
        focusedIndicatorColor = VerdeEsmeralda, // Borde cuando está enfocado
        unfocusedIndicatorColor = GrisOscuro.copy(alpha = 0.5f), // Borde cuando no está enfocado
        focusedLabelColor = GrisOscuro,
        unfocusedLabelColor = GrisOscuro,
        cursorColor = VerdeEsmeralda
    )
}
