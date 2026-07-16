package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterStepIndicator(
    currentStep: Int,
    totalSteps: Int = 3
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..totalSteps) {
            val isActive = step == currentStep
            val isCompleted = step < currentStep

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> Color(0xFF4CAF50)
                            isActive -> Color(0xFFD32F2F)
                            else -> Color(0xFFE0E0E0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCompleted) "✓" else step.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (step < totalSteps) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(if (isCompleted) Color(0xFF4CAF50) else Color(0xFFE0E0E0))
                )
            }
        }
    }
}