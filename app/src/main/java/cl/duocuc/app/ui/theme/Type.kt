package cl.duocuc.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import cl.duocuc.app.R

//Proveedor de Google Fonts
val provider = Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)


val montserrat = GoogleFont("Montserrat")
val playfair = GoogleFont("Playfair Display")


val MontserratFontFamily = FontFamily(
    Font(googleFont = montserrat, fontProvider = provider, weight = FontWeight.Normal),     // Regular
    Font(googleFont = montserrat, fontProvider = provider, weight = FontWeight.Medium),     // Medium
    Font(googleFont = montserrat, fontProvider = provider, weight = FontWeight.SemiBold)    // SemiBold
)

val PlayfairFontFamily = FontFamily(
    Font(googleFont = playfair, fontProvider = provider, weight = FontWeight.Normal), // Regular
    Font(googleFont = playfair, fontProvider = provider, weight = FontWeight.Bold)    // Bold
)

val Typography = Typography(

    // --- Textos generales ---
    bodyLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),

    // --- Títulos y encabezados ---
    titleLarge = TextStyle(
        fontFamily = PlayfairFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PlayfairFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    )
)
