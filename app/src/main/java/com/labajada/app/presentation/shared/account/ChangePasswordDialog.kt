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
import com.labajada.app.core.validation.PasswordValidator
import com.labajada.app.domain.repository.UserRepository
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordDialog(
    userRepository: UserRepository,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    // Solo se vuelve true si el repositorio confirmó el cambio (result.onSuccess).
    // Mientras esto sea false, no se muestra ningún aviso de éxito.
    var passwordChanged by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val passwordCheck = remember(newPassword) { PasswordValidator.validate(newPassword) }
    val passwordsMatch = confirmPassword.isEmpty() || newPassword == confirmPassword

    if (passwordChanged) {
        AlertDialog(
            onDismissRequest = onSuccess,
            title = { Text("¡Listo!", fontWeight = FontWeight.Bold) },
            text = { Text("Tu contraseña se cambió correctamente.") },
            confirmButton = {
                Button(onClick = onSuccess) { Text("Entendido") }
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Cambiar contraseña", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Por seguridad, ingresa tu contraseña actual antes de establecer una nueva.",
                    fontSize = 13.sp
                )
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it; error = null },
                    label = { Text("Contraseña actual") },
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it; error = null },
                    label = { Text("Nueva contraseña") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = newPassword.isNotEmpty() && !passwordCheck.isValid,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; error = null },
                    label = { Text("Confirmar nueva contraseña") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = confirmPassword.isNotEmpty() && !passwordsMatch,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading && currentPassword.isNotBlank() && passwordCheck.isValid && passwordsMatch && confirmPassword.isNotBlank(),
                onClick = {
                    scope.launch {
                        isLoading = true
                        error = null
                        val result = userRepository.changePassword(currentPassword, newPassword)
                        isLoading = false
                        result.onSuccess {
                            // Solo se marca "cambiado" en la rama de éxito real del Result.
                            passwordChanged = true
                        }.onFailure {
                            error = "No se pudo cambiar la contraseña. Verifica tu contraseña actual."
                        }
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Cambiar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancelar") }
        }
    )
}