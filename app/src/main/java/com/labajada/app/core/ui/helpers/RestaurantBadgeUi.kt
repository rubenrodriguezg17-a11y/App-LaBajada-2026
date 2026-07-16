package com.labajada.app.core.ui.helpers

import androidx.compose.ui.graphics.Color

enum class RestaurantBadgeLevel(val etiqueta: String, val color: Color) {
    NINGUNO("", Color.Transparent),
    BASICO("Básico", Color(0xFF607D8B)),           // gris azulado — 1 documento subido
    CONFIABLE("Confiable", Color(0xFF43A047)),      // verde — 2 documentos
    VERIFICADO("Verificado", Color(0xFF1E88E5)),    // azul — 3 documentos
    EMPRESA("Empresa Verificada", Color(0xFFFFB300)) // oro — RUC + validado por un admin
}

fun calcularNivelInsignia(
    documentType: String,
    isVerified: Boolean,
    documentsSubmittedAt: Long?,
    storePhotoUrl: String?,
    menuPhotoUrl: String?,
    permitPhotoUrl: String?
): RestaurantBadgeLevel {
    val enRevision = documentsSubmittedAt != null && !isVerified
    if (enRevision) return RestaurantBadgeLevel.BASICO

    if (documentType == "RUC" && isVerified) return RestaurantBadgeLevel.EMPRESA

    val documentosSubidos = listOfNotNull(storePhotoUrl, menuPhotoUrl, permitPhotoUrl).size
    return when (documentosSubidos) {
        3 -> RestaurantBadgeLevel.VERIFICADO
        2 -> RestaurantBadgeLevel.CONFIABLE
        1 -> RestaurantBadgeLevel.BASICO
        else -> RestaurantBadgeLevel.NINGUNO
    }
}