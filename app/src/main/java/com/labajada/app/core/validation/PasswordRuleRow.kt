package com.labajada.app.core.validation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PasswordRuleRow(text: String, fulfilled: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (fulfilled) "✓" else "✗",
            color = if (fulfilled) Color(0xFF2E7D32) else Color(0xFFBDBDBD),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(18.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (fulfilled) Color(0xFF2E7D32) else Color(0xFF9E9E9E)
        )
    }
}