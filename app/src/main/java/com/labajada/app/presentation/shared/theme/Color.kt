package com.labajada.app.presentation.shared.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Fondo Base (Marfil cálido, no blanco clínico) — pantallas del comprador
val IvoryBackground = Color(0xFFFBF9F5)
val SurfaceCardClara = Color(0xFFFFFFFF)

// Fondo cálido — pantallas del lado restaurante (registro, dashboard)
val FondoCalidoRestaurante = Color(0xFFFBF3E6)

// Modo Oscuro / Noche (Negro carbón, no negro puro)
val CharcoalBackground = Color(0xFF0F1113)
val SurfaceCardOscura = Color(0xFF161A1D)

// Colores de Marca y Acento
val RojoGochujang = Color(0xFFC83328)      // Apetito: logo, acciones de compra
val NaranjaCercania = Color(0xFFE07A2F)    // CTA accesible: registro de comprador
val DoradoTostado = Color(0xFFE5A93B)      // Registro de restaurante, etiquetas especiales
val MarronSazon = Color(0xFF7A3E10)        // Acento artesanal en pantallas de restaurante
val VerdeMatcha = Color(0xFF1B4332)        // Confianza, éxito, login, "abierto"

// Mantenido por compatibilidad con código existente que usa OroLiquido
val OroLiquido = DoradoTostado

// Textos, bordes y campos
val TextoPrincipal = Color(0xFF1C1E21)
val TextoSecundario = Color(0xFF65696E)
val TextoSecundarioRestaurante = Color(0xFF8A6A48)
val BordeSuave = Color(0xFFE4E6EB)
val BordeCalidoRestaurante = Color(0xFFEAD9BC)
val SuperficieCampo = Color(0xFFF2F0EB)

// Estados
val RojoAlerta = Color(0xFFB71C1C)

// Negro — uso funcional (datos críticos, botones de alto peso), no decorativo. Ver guía de uso.
val NegroContorno = Color(0xFF1C1E21)

val LightColorScheme = lightColorScheme(
    primary = RojoGochujang,
    secondary = VerdeMatcha,
    tertiary = DoradoTostado,
    background = IvoryBackground,
    surface = SurfaceCardClara,
    error = RojoAlerta,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = MarronSazon,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal,
    onError = Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = RojoGochujang,
    secondary = VerdeMatcha,
    tertiary = DoradoTostado,
    background = CharcoalBackground,
    surface = SurfaceCardOscura,
    error = RojoAlerta,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)