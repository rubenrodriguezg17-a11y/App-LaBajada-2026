package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardHeader(
    pedidosEnCola: Int,
    gananciasHoy: Double,
    isGananciasVisible: Boolean,
    onToggleGanancias: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            title = "Pedidos en Cola",
            value = "$pedidosEnCola",
            modifier = Modifier.weight(1f)
        )

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ganancias Hoy",
                        fontSize = 11.sp,
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedContent(
                        targetState = isGananciasVisible,
                        label = "ganancias_visibility",
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { visible ->
                        Text(
                            text = if (visible) "S/. ${String.format("%.2f", gananciasHoy)}" else "S/. ••••",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF263238)
                        )
                    }
                }
                IconButton(onClick = onToggleGanancias, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isGananciasVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isGananciasVisible) "Ocultar ganancias" else "Mostrar ganancias",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}