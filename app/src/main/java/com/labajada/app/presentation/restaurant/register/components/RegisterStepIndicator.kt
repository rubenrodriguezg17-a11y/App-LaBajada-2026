package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private val PasoCompletado = Color(0xFFE5A93B)
private val GrisPendiente = Color(0xFFD9D9D9)
private val TextoSobreDorado = Color(0xFF7A3E10)

@Composable
fun RegisterStepIndicator(
    currentStep: Int,
    totalSteps: Int = 4
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..totalSteps) {
            val isCompleted = step < currentStep
            val isActive = step == currentStep

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted || isActive) PasoCompletado else GrisPendiente),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCompleted) "✓" else step.toString(),
                    color = if (isCompleted || isActive) TextoSobreDorado else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (step < totalSteps) {
                CurvedArrow(
                    color = if (isCompleted) PasoCompletado else GrisPendiente,
                    modifier = Modifier.size(width = 44.dp, height = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun CurvedArrow(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val startX = 0f
        val startY = size.height * 0.75f
        val endX = size.width
        val endY = size.height * 0.25f

        val controlX = size.width * 0.5f
        val controlY = size.height * 1.15f

        val path = Path().apply {
            moveTo(startX, startY)
            quadraticBezierTo(controlX, controlY, endX, endY)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx())
        )

        val angle = atan2((endY - controlY).toDouble(), (endX - controlX).toDouble())
        val arrowLength = 7.dp.toPx()
        val arrowAngle = Math.toRadians(28.0)

        val p1 = Offset(
            x = endX - (arrowLength * cos(angle - arrowAngle)).toFloat(),
            y = endY - (arrowLength * sin(angle - arrowAngle)).toFloat()
        )
        val p2 = Offset(
            x = endX - (arrowLength * cos(angle + arrowAngle)).toFloat(),
            y = endY - (arrowLength * sin(angle + arrowAngle)).toFloat()
        )

        drawPath(
            path = Path().apply {
                moveTo(endX, endY)
                lineTo(p1.x, p1.y)
                moveTo(endX, endY)
                lineTo(p2.x, p2.y)
            },
            color = color,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}