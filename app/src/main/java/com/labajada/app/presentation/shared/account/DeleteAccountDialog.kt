package com.labajada.app.presentation.shared.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun DeleteAccountDialog(
    onConfirmDeactivate: suspend (password: String) -> Result<Unit>,
    onDismiss: () -> Unit,
    onAccountDeactivated: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(if (step == 1) "Desactivar cuenta" else "¿Estás segura?", fontWeight = FontWeight.Bold)
        },
        text = {
            if (step == 1) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ingresa tu contraseña para continuar. Tu cuenta dejará de estar disponible.", fontSize = 13.sp)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (error != null) {
                        Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    }
                }
            } else {
                Text(
                    "Al desactivar tu cuenta, ya no podrás iniciar sesión ni aparecerás en La Bajada. Si quieres volver, contáctanos por soporte.",
                    fontSize = 13.sp
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading && (step == 2 || password.isNotBlank()),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                onClick = {
                    if (step == 1) {
                        step = 2
                    } else {
                        scope.launch {
                            isLoading = true
                            error = null
                            val result = onConfirmDeactivate(password)
                            isLoading = false
                            result.onSuccess {
                                onAccountDeactivated()
                            }.onFailure {
                                error = "No se pudo desactivar la cuenta. Verifica tu contraseña."
                                step = 1
                            }
                        }
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (step == 1) "Continuar" else "Sí, desactivar mi cuenta")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { if (step == 2) step = 1 else onDismiss() }, enabled = !isLoading) {
                Text(if (step == 2) "Volver" else "Cancelar")
            }
        }
    )
}