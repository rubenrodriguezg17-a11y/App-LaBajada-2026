package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.shared.legal.TermsAndConditionsDialog

@Composable
fun TermsAcceptanceRow(
    acceptedTerms: Boolean,
    onAcceptedTermsChange: (Boolean) -> Unit
) {
    var showTerms by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Checkbox(
            checked = acceptedTerms,
            onCheckedChange = onAcceptedTermsChange,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF263238))
        )
        Text("Acepto los ", fontSize = 13.sp, color = Color.Gray)
        Text(
            text = "Términos y Condiciones",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238),
            modifier = Modifier.clickable { showTerms = true }
        )
    }

    if (showTerms) {
        TermsAndConditionsDialog(
            isSeller = true,
            onDismiss = { showTerms = false },
            onAccept = {
                onAcceptedTermsChange(true)
                showTerms = false
            }
        )
    }
}