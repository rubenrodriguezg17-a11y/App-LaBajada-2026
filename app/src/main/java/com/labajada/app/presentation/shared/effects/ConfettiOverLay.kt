package com.labajada.app.presentation.shared.effects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiPiece(
    val startX: Float,
    val color: Color,
    val fallDelay: Long,
    val angleOffset: Float,
    val sizeFactor: Float
)

private val confettiColors = listOf(
    Color(0xFFD32F2F), Color(0xFFFFC107), Color(0xFF4CAF50),
    Color(0xFF2196F3), Color(0xFF9C27B0), Color(0xFFFF9800)
)

@Composable
fun ConfettiOverlay(trigger: Long) {
    if (trigger == 0L) return

    var isPlaying by remember { mutableStateOf(false) }
    val pieces = remember(trigger) {
        List(45) {
            ConfettiPiece(
                startX = Random.nextFloat(),
                color = confettiColors.random(),
                fallDelay = Random.nextLong(0, 300),
                angleOffset = Random.nextFloat() * 360f,
                sizeFactor = Random.nextFloat() * 0.6f + 0.6f
            )
        }
    }
    val progress = remember(trigger) { Animatable(0f) }

    LaunchedEffect(trigger) {
        isPlaying = true
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(2200, easing = LinearEasing))
        isPlaying = false
    }

    if (!isPlaying) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val h = size.height
        val w = size.width

        pieces.forEach { piece ->
            val localProgress = ((progress.value * 2200 - piece.fallDelay) / 1900f).coerceIn(0f, 1f)
            if (localProgress <= 0f) return@forEach

            val y = -60f + localProgress * (h + 120f)
            val sway = sin((localProgress * 6f) + piece.angleOffset) * 40f
            val x = piece.startX * w + sway
            val rotation = piece.angleOffset + localProgress * 720f
            val alpha = if (localProgress > 0.85f) (1f - localProgress) / 0.15f else 1f

            rotate(degrees = rotation, pivot = Offset(x, y)) {
                drawRect(
                    color = piece.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                    topLeft = Offset(x - 6f * piece.sizeFactor, y - 10f * piece.sizeFactor),
                    size = Size(12f * piece.sizeFactor, 20f * piece.sizeFactor)
                )
            }
        }
    }
}