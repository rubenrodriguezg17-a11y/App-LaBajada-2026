package com.labajada.app.core.ui.helpers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RestaurantBadgeChip(nivel : RestaurantBadgeLevel){
    if (nivel == RestaurantBadgeLevel.NINGUNO) return

    Card(
        colors = CardDefaults.cardColors(containerColor = nivel.color.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = nivel.etiqueta,
                tint = nivel.color,
                modifier = Modifier.padding(end = 3.dp).size(12.dp)
            )
            Text(
                text = nivel.etiqueta,
                fontSize = 10.sp,
                color = nivel.color
            )
        }
    }
}