package com.labajada.app.presentation.shared.cropper

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.labajada.app.core.utils.loadBitmapFromUri
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Recortador estilo WhatsApp: arrastra para mover, pellizca para hacer zoom,
 * dentro de un marco fijo con la proporción indicada. Al confirmar, entrega
 * exactamente lo que se ve dentro del marco como Bitmap ya recortado.
 */
@Composable
fun ImageCropperDialog(
    imageUri: Uri,
    aspectRatio: Float,
    onCancel: () -> Unit,
    onCropped: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    var sourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(imageUri) {
        sourceBitmap = loadBitmapFromUri(context, imageUri)
    }

    val framePaddingDp = 24.dp
    val frameWidthDp = configuration.screenWidthDp.dp - (framePaddingDp * 2)
    val frameHeightDp = frameWidthDp / aspectRatio
    val frameWidthPx = with(density) { frameWidthDp.toPx() }
    val frameHeightPx = with(density) { frameHeightDp.toPx() }

    var userScale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            val bitmap = sourceBitmap
            if (bitmap == null) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.Center))
            } else {
                val baseScale = remember(bitmap) {
                    max(frameWidthPx / bitmap.width, frameHeightPx / bitmap.height)
                }

                fun clamp(raw: Offset, scale: Float): Offset {
                    val dw = bitmap.width * baseScale * scale
                    val dh = bitmap.height * baseScale * scale
                    return Offset(
                        x = raw.x.coerceIn(frameWidthPx - dw, 0f),
                        y = raw.y.coerceIn(frameHeightPx - dh, 0f)
                    )
                }

                LaunchedEffect(bitmap) {
                    val dw = bitmap.width * baseScale
                    val dh = bitmap.height * baseScale
                    offset = Offset(
                        x = ((frameWidthPx - dw) / 2f).coerceIn(frameWidthPx - dw, 0f),
                        y = ((frameHeightPx - dh) / 2f).coerceIn(frameHeightPx - dh, 0f)
                    )
                    userScale = 1f
                }

                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onCancel) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = Color.White)
                        }
                        Text("Ajusta tu foto", color = Color.White, fontWeight = FontWeight.Bold)
                        IconButton(
                            onClick = {
                                if (!isProcessing) {
                                    isProcessing = true
                                    val cropped = cropBitmap(
                                        source = bitmap,
                                        baseScale = baseScale,
                                        userScale = userScale,
                                        offset = offset,
                                        frameWidthPx = frameWidthPx,
                                        frameHeightPx = frameHeightPx
                                    )
                                    onCropped(cropped)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Confirmar", tint = Color(0xFF4CAF50))
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .width(frameWidthDp)
                            .height(frameHeightDp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B1B1B))
                    ) {
                        val dw = bitmap.width * baseScale * userScale
                        val dh = bitmap.height * baseScale * userScale
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Recortar imagen",
                            contentScale = ContentScale.None,
                            modifier = Modifier
                                .size(
                                    width = with(density) { dw.toDp() },
                                    height = with(density) { dh.toDp() }
                                )
                                .graphicsLayer { translationX = offset.x; translationY = offset.y }
                                .pointerInput(bitmap) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        val newScale = (userScale * zoom).coerceIn(1f, 4f)
                                        offset = clamp(offset + pan, newScale)
                                        userScale = newScale
                                    }
                                }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Arrastra y pellizca para ajustar",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}

private fun cropBitmap(
    source: Bitmap,
    baseScale: Float,
    userScale: Float,
    offset: Offset,
    frameWidthPx: Float,
    frameHeightPx: Float
): Bitmap {
    val totalScale = baseScale * userScale
    val cropLeft = (-offset.x / totalScale).roundToInt().coerceIn(0, source.width - 1)
    val cropTop = (-offset.y / totalScale).roundToInt().coerceIn(0, source.height - 1)
    val cropWidth = (frameWidthPx / totalScale).roundToInt().coerceIn(1, source.width - cropLeft)
    val cropHeight = (frameHeightPx / totalScale).roundToInt().coerceIn(1, source.height - cropTop)
    return Bitmap.createBitmap(source, cropLeft, cropTop, cropWidth, cropHeight)
}