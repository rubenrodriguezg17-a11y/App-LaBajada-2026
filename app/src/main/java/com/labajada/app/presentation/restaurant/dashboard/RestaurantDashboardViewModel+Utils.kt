package com.labajada.app.presentation.restaurant.dashboard

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Utilidades compartidas por más de una sección del dashboard (perfil y menú de
 * platillos ambos guardan imágenes elegidas por el usuario en almacenamiento interno).
 * `internal` porque se usa desde varios archivos de extensión del mismo paquete.
 */
internal fun RestaurantDashboardViewModel.guardarImagenEnAlmacenamientoInterno(
    context: Context,
    uri: Uri
): String? {
    return try {
        val fileName = "platillo_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        context.contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream -> inputStream?.copyTo(outputStream) }
        }
        file.absolutePath
    } catch (e: Exception) {
        android.util.Log.e("RestaurantDashboardViewModel", "Error al guardar imagen en almacenamiento interno", e)
        null
    }
}
