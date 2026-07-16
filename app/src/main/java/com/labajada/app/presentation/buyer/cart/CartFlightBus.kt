package com.labajada.app.presentation.buyer.cart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

data class FlightEvent(
    val id: String = UUID.randomUUID().toString(),
    val start: Offset
)

object CartFlightBus {
    var cartAnchor by mutableStateOf<Offset?>(null)
    val events = mutableStateListOf<FlightEvent>()

    fun launchFlight(start: Offset) {
        events.add(FlightEvent(start = start))
    }

    fun consume(event: FlightEvent) {
        events.remove(event)
    }
}

@Composable
fun FlyToCartOverlay(onLanded: () -> Unit = {}) {
    val target = CartFlightBus.cartAnchor ?: return
    var overlayOrigin by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { overlayOrigin = it.positionInWindow()
                android.util.Log.d("CartFlight", "overlayOrigin = $overlayOrigin | target = $target")
            }

    ) {
        CartFlightBus.events.toList().forEach { event ->
            key(event.id) {
                FlyingDot(
                    start = event.start - overlayOrigin,
                    end = target - overlayOrigin,
                    onFinished = {
                        CartFlightBus.consume(event)
                        onLanded()
                    }
                )
            }
        }
    }
}

@Composable
private fun FlyingDot(start: Offset, end: Offset, onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val baseRadius = 12.dp

    LaunchedEffect(Unit) {
        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(1200, easing = EaseInOutCubic)
            )
            onFinished()
        }
        launch {
            delay(800)
            scale.animateTo(
                targetValue = 0f,
                animationSpec = tween(400, easing = EaseInOutCubic)
            )
        }
    }

    val t = progress.value
    val x = start.x + (end.x - start.x) * t
    val arcHeight = -180f * (4 * t * (1 - t))
    val y = start.y + (end.y - start.y) * t + arcHeight

    Canvas(modifier = Modifier.fillMaxSize()) {
        val radiusPx = baseRadius.toPx()

        translate(
            left = x - (radiusPx * scale.value),
            top = y - (radiusPx * scale.value)
        ) {
            drawCircle(
                color = Color(0xFFD32F2F),
                radius = radiusPx * scale.value,
                center = Offset.Zero
            )
        }
    }
}
