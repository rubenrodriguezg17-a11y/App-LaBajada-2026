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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PasswordRuleRow
import com.labajada.app.core.validation.PasswordValidator
import com.labajada.app.presentation.shared.theme.*

@Composable
fun RegisterCredentialsStep(
    ownerFullName: String,
    email: String,
    password: String,
    confirmPassword: String,
    onOwnerFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isEmailValid = remember(email) { email.isEmpty() || PasswordValidator.isValidEmail(email) }
    val passwordCheck = remember(password) { PasswordValidator.validate(password) }
    val showPasswordChecklist = password.isNotEmpty()
    val passwordsMatch = confirmPassword.isEmpty() || password == confirmPassword

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Por último...",
            fontSize = 27.sp,
            fontFamily = Bangers,
            color = MarronSazon
        )
        Text(
            text = "Ahora te toca a ti, debes agregar tu información personal, como dueño del negocio",
            fontSize = 14.sp,
            fontFamily = Nunito,
            color = TextoSecundarioRestaurante
        )
        Spacer(modifier = Modifier.height(4.dp))
        RegisterStepIndicator(currentStep = 4, totalSteps = 4)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "NOMBRE COMPLETO:",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            OutlinedTextField(
                value = ownerFullName,
                onValueChange = onOwnerFullNameChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (ownerFullName.isNotBlank()) DoradoTostado else BordeCalidoRestaurante,
                    unfocusedBorderColor = if (ownerFullName.isNotBlank()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "CORREO ELECTRÓNICO (EMAIL)",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = !isEmailValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (!isEmailValid) RojoAlerta else if (email.isNotEmpty()) DoradoTostado else BordeCalidoRestaurante,
                    unfocusedBorderColor = if (!isEmailValid) RojoAlerta else if (email.isNotEmpty()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                )
            )
            if (!isEmailValid) {
                Text(
                    text = "Ingresa un formato de correo válido",
                    color = RojoAlerta,
                    fontSize = 12.sp,
                    fontFamily = Nunito,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "CONTRASEÑA",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = showPasswordChecklist && !passwordCheck.isValid,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (showPasswordChecklist && !passwordCheck.isValid) RojoAlerta else if (password.isNotEmpty()) DoradoTostado else BordeCalidoRestaurante,
                    unfocusedBorderColor = if (showPasswordChecklist && !passwordCheck.isValid) RojoAlerta else if (password.isNotEmpty()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                )
            )
            if (showPasswordChecklist) {
                Column(
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    PasswordRuleRow("Mínimo 8 caracteres", passwordCheck.hasMinLength)
                    PasswordRuleRow("Una letra mayúscula", passwordCheck.hasUppercase)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "CONFIRMAR CONTRASEÑA",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = confirmPassword.isNotEmpty() && !passwordsMatch,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (confirmPassword.isNotEmpty() && !passwordsMatch) RojoAlerta else if (confirmPassword.isNotEmpty()) DoradoTostado else BordeCalidoRestaurante,
                    unfocusedBorderColor = if (confirmPassword.isNotEmpty() && !passwordsMatch) RojoAlerta else if (confirmPassword.isNotEmpty()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                )
            )
            if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = RojoAlerta,
                    fontSize = 12.sp,
                    fontFamily = Nunito,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}