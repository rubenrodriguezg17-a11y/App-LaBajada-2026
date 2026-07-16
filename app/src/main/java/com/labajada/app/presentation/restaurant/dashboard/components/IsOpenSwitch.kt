package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IsOpenSwitch(
    isOpen: Boolean,
    onToggle: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
        animationSpec = tween(300),
        label = "is_open_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828),
        animationSpec = tween(300),
        label = "is_open_text"
    )
    val trackColor by animateColorAsState(
        targetValue = if (isOpen) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
        animationSpec = tween(300),
        label = "is_open_track"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isOpen) "¡Abierto ahora!" else "Cerrado por el momento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = textColor
                )
                Text(
                    text = if (isOpen) "Los clientes pueden ver tu menú y ordenar."
                    else "Tu local no aparece en las búsquedas activas.",
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.8f)
                )
            }
            Switch(
                checked = isOpen,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = trackColor,
                    checkedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE0E0E0),
                    uncheckedThumbColor = Color.White
                ),
                modifier = Modifier.size(56.dp, 32.dp)
            )
        }
    }
}