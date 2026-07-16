package com.labajada.app.presentation.shared.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Fondo Base (Marfil cálido, no blanco clínico)
val IvoryBackground = Color(0xFFFBF9F5)
val SurfaceCardClara = Color(0xFFFFFFFF)

// Modo Oscuro / Noche (Negro carbón, no negro puro)
val CharcoalBackground = Color(0xFF0F1113)
val SurfaceCardOscura = Color(0xFF161A1D)

// Colores de Marca y Acento
val RojoGochujang = Color(0xFFC83328)    // Rojo elegante que abre el apetito
val VerdeMatcha = Color(0xFF1B4332)      // Estados de éxito y confianza
val OroLiquido = Color(0xFFE5A93B)       // Etiquetas especiales (Let's Go)

// Textos y Bordes
val TextoPrincipal = Color(0xFF1C1E21)
val TextoSecundario = Color(0xFF65696E)
val BordeSuave = Color(0xFFE4E6EB)

private val LightColorScheme = lightColorScheme(
    primary = RojoGochujang,
    secondary = VerdeMatcha,
    background = IvoryBackground,
    surface = SurfaceCardClara,
    onPrimary = Color.White,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal
)

private val DarkColorScheme = darkColorScheme(
    primary = RojoGochujang,
    secondary = VerdeMatcha,
    background = CharcoalBackground,
    surface = SurfaceCardOscura,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)