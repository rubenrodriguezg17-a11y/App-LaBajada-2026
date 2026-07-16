package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PasswordRuleRow
import com.labajada.app.core.validation.PasswordValidator

@Composable
fun RegisterCredentialsStep(
    ownerFullName: String,
    email: String,
    password: String,
    confirmPassword: String,
    onOwnerFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,

    acceptedTerms: Boolean,
    onAcceptedTermsChange: (Boolean) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isEmailValid = remember(email) {
        email.isEmpty() || PasswordValidator.isValidEmail(email)
    }
    val passwordCheck = remember(password) {
        PasswordValidator.validate(password)
    }
    val showPasswordChecklist = password.isNotEmpty()
    val passwordsMatch = confirmPassword.isEmpty() || password == confirmPassword

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Datos de acceso",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF263238)
        )
        Text(
            text = "Esta será tu cuenta para iniciar sesión en el dashboard.",
            fontSize = 13.sp,
            color = Color.Gray
        )

        OutlinedTextField(
            value = ownerFullName,
            onValueChange = onOwnerFullNameChange,
            label = { Text("Tu nombre completo") },
            placeholder = { Text("Ej: María Rodríguez") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = !isEmailValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        if (!isEmailValid) {
            Text(
                text = "Ingresa un formato de correo válido",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = showPasswordChecklist && !passwordCheck.isValid,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        )

        if (showPasswordChecklist) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                PasswordRuleRow("Mínimo 8 caracteres", passwordCheck.hasMinLength)
                PasswordRuleRow("Una letra mayúscula", passwordCheck.hasUppercase)
            }
        }

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = confirmPassword.isNotEmpty() && !passwordsMatch,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        )
        if (confirmPassword.isNotEmpty() && !passwordsMatch) {
            Text(
                text = "Las contraseñas no coinciden",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        TermsAcceptanceRow(
            acceptedTerms = acceptedTerms,
            onAcceptedTermsChange = onAcceptedTermsChange
        )
    }
}