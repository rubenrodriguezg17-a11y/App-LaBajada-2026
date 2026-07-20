package com.labajada.app.presentation.shared.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.labajada.app.R

// Bangers — wordmark, hero de onboarding, saludo de login. Uso puntual, nunca en párrafos.
val Bangers = FontFamily(
    Font(R.font.bangers_regular)
)

// Baloo 2 — títulos de pantalla, nombres de restaurante/plato, botones. Cálida pero legible.
// NOTA: agregar los archivos .ttf en res/font/ con estos nombres exactos (descargar de Google Fonts).
val Baloo2 = FontFamily(
    Font(R.font.baloo2_semibold, FontWeight.SemiBold),
    Font(R.font.baloo2_bold, FontWeight.Bold),
    Font(R.font.baloo2_extrabold, FontWeight.ExtraBold)
)

// Nunito — cuerpo de texto, inputs, labels. La más legible de las tres, base de la app.
val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold)
)

val LaBajadaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Bangers,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Baloo2,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Baloo2,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Baloo2,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
)
