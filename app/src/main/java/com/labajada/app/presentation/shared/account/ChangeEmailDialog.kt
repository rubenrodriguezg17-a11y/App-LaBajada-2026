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
fun ChangeEmailDialog(
    userRepository: UserRepository,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    // Solo se vuelve true si el repositorio confirmó el cambio (result.onSuccess).
    var emailChanged by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val isEmailValid = newEmail.isEmpty() || PasswordValidator.isValidEmail(newEmail)

    if (emailChanged) {
        AlertDialog(
            onDismissRequest = onSuccess,
            title = { Text("¡Listo!", fontWeight = FontWeight.Bold) },
            text = { Text("Tu correo ha sido cambiado correctamente. Revisa tu Gmail para confirmar el cambio.") },
            confirmButton = {
                Button(onClick = onSuccess) { Text("Entendido") }
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Cambiar correo electrónico", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Te enviaremos un enlace de confirmación al nuevo correo. El cambio se aplicará cuando lo confirmes.",
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
                    value = newEmail,
                    onValueChange = { newEmail = it; error = null },
                    label = { Text("Nuevo correo electrónico") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = newEmail.isNotEmpty() && !isEmailValid,
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
                enabled = !isLoading && currentPassword.isNotBlank() && newEmail.isNotBlank() && isEmailValid,
                onClick = {
                    scope.launch {
                        isLoading = true
                        error = null
                        val result = userRepository.changeEmail(currentPassword, newEmail.trim())
                        isLoading = false
                        result.onSuccess {
                            // Solo se marca "cambiado" en la rama de éxito real del Result.
                            emailChanged = true
                        }.onFailure { e ->
                            error = e.message ?: "No se pudo cambiar el correo. Intenta de nuevo."
                        }
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Enviar confirmación")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancelar") }
        }
    )
}