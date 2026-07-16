package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CollapsibleStatusSection(
    isOpen: Boolean,
    onToggleIsOpen: () -> Unit
) {
    var isHeaderExpanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {

        // Barrita siempre visible
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isHeaderExpanded = !isHeaderExpanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Estado del restaurante: ",
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
                Text(
                    text = if (isOpen) "ABIERTO" else "CERRADO",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOpen) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                )
            }
            Text(
                text = if (isHeaderExpanded) "▲ Ocultar" else "▼ Cambiar",
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )
        }

        // Persiana con tu IsOpenSwitch dentro
        AnimatedVisibility(
            visible = isHeaderExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Box(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)) {
                IsOpenSwitch(
                    isOpen = isOpen,
                    onToggle = {
                        if (isOpen) {
                            // Va a APAGAR → pide confirmación
                            showConfirmDialog = true
                        } else {
                            // Va a PRENDER → directo, sin fricción
                            onToggleIsOpen()
                            isHeaderExpanded = false
                        }
                    }
                )
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("¿Cerrar tu local?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Se ocultará tu menú en La Bajada y no podrás recibir nuevos pedidos hasta que vuelvas a abrir.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleIsOpen()
                        showConfirmDialog = false
                        isHeaderExpanded = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Sí, cerrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}