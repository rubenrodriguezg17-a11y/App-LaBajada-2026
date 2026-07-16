package com.labajada.app.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

/**
 * Carga una imagen desde un Uri como Bitmap real en memoria, ya corregida de
 * rotación (EXIF) y reducida si es gigante, para evitar OutOfMemory y fotos
 * de varios MB guardadas sin necesidad.
 */
fun loadBitmapFromUri(context: Context, uri: Uri, maxDimension: Int = 2000): Bitmap? {
    return try {
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, boundsOptions)
        }

        var sampleSize = 1
        while ((boundsOptions.outWidth / sampleSize) > maxDimension ||
            (boundsOptions.outHeight / sampleSize) > maxDimension
        ) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val decoded = context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, decodeOptions)
        } ?: return null

        val rotationDegrees = context.contentResolver.openInputStream(uri)?.use { input ->
            when (ExifInterface(input).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } ?: 0

        if (rotationDegrees == 0) {
            decoded
        } else {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
        }
    } catch (e: Exception) {
        android.util.Log.e("ImageCropUtils", "Error cargando bitmap desde Uri", e)
        null
    }
}

/** Comprime y guarda el bitmap ya recortado en el almacenamiento interno. */
fun saveBitmapToInternalStorage(
    context: Context,
    bitmap: Bitmap,
    fileName: String,
    quality: Int = 85
): String? {
    return try {
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
        }
        file.absolutePath
    } catch (e: Exception) {
        android.util.Log.e("ImageCropUtils", "Error guardando bitmap en almacenamiento interno", e)
        null
    }
}