package com.labajada.app.presentation.restaurant.dashboard.components.modals

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DeleteDishDialog(
    itemId: String, // Cambia el tipo (String/Int) según corresponda a tu proyecto
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Eliminar platillo?") },
        text = { Text("Esta acción quitará el platillo de tu menú del día.") },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(itemId.toString()) }
            ) {
                Text("Eliminar", color = Color(0xFFD32F2F))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
