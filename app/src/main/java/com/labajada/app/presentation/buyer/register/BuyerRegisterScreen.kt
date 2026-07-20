package com.labajada.app.presentation.buyer.register

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PasswordValidator
import com.labajada.app.core.validation.PeruValidators
import com.labajada.app.core.validation.PasswordRuleRow
import com.labajada.app.presentation.shared.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerRegisterScreen(
    viewModel: BuyerRegisterViewModel,
    onRegistrationComplete: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val isEmailValid = remember(uiState.email) {
        uiState.email.isEmpty() || PasswordValidator.isValidEmail(uiState.email)
    }
    val isPhoneValid = remember(uiState.phone) {
        uiState.phone.isEmpty() || PeruValidators.isValidPhone(uiState.phone)
    }
    val passwordCheck = remember(uiState.password) {
        PasswordValidator.validate(uiState.password)
    }
    val showPasswordChecklist = uiState.password.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IvoryBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera — Naranja Cercanía domina el registro de comprador: accesible, rápido, sin fricción.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "¡Estás a un paso!",
                    fontSize = 30.sp,
                    fontFamily = Bangers,
                    color = NaranjaCercania,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Queremos conocerte para mostrarte un huarique al toque.",
                    fontSize = 15.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    color = TextoSecundario,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tarjeta Contenedor (Agrupa el formulario)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Tu Nombre o Apodo", fontFamily = Nunito, fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SuperficieCampo,
                            unfocusedContainerColor = SuperficieCampo,
                            focusedBorderColor = if (uiState.name.isNotBlank()) VerdeMatcha else BordeSuave,
                            unfocusedBorderColor = BordeSuave,
                            cursorColor = NaranjaCercania,
                            focusedLabelColor = TextoPrincipal
                        )
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.phone,
                            onValueChange = { if (it.length <= 9 && it.all(Char::isDigit)) viewModel.onPhoneChange(it) },
                            label = { Text("Número de Celular", fontFamily = Nunito, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            isError = !isPhoneValid,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SuperficieCampo,
                                unfocusedContainerColor = SuperficieCampo,
                                focusedBorderColor = if (isPhoneValid && uiState.phone.isNotEmpty()) VerdeMatcha else RojoAlerta,
                                unfocusedBorderColor = if (isPhoneValid && uiState.phone.isNotEmpty()) VerdeMatcha.copy(alpha = 0.5f) else BordeSuave,
                                cursorColor = NaranjaCercania,
                                focusedLabelColor = TextoPrincipal
                            )
                        )
                        if (!isPhoneValid) {
                            Text(
                                text = "Celular inválido (9 dígitos, empieza con 9)",
                                color = RojoAlerta,
                                fontSize = 12.sp,
                                fontFamily = Nunito,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            label = { Text("Correo Electrónico", fontFamily = Nunito, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            isError = !isEmailValid,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SuperficieCampo,
                                unfocusedContainerColor = SuperficieCampo,
                                focusedBorderColor = if (isEmailValid && uiState.email.isNotEmpty()) VerdeMatcha else RojoAlerta,
                                unfocusedBorderColor = if (isEmailValid && uiState.email.isNotEmpty()) VerdeMatcha.copy(alpha = 0.5f) else BordeSuave,
                                cursorColor = NaranjaCercania,
                                focusedLabelColor = TextoPrincipal
                            )
                        )
                        if (!isEmailValid) {
                            Text(
                                text = "Ingresa un formato de correo válido",
                                color = RojoAlerta,
                                fontSize = 12.sp,
                                fontFamily = Nunito,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            label = { Text("Contraseña", fontFamily = Nunito, fontWeight = FontWeight.SemiBold) },
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
                                focusedContainerColor = SuperficieCampo,
                                unfocusedContainerColor = SuperficieCampo,
                                focusedBorderColor = if (passwordCheck.isValid) VerdeMatcha else RojoAlerta,
                                unfocusedBorderColor = if (passwordCheck.isValid) VerdeMatcha.copy(alpha = 0.5f) else BordeSuave,
                                cursorColor = NaranjaCercania,
                                focusedLabelColor = TextoPrincipal
                            )
                        )
                        if (showPasswordChecklist) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp, top = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                PasswordRuleRow("Mínimo 8 caracteres", passwordCheck.hasMinLength)
                                PasswordRuleRow("Una letra mayúscula", passwordCheck.hasUppercase)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer con botón e interruptores legales
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var showTerms by remember { mutableStateOf(false) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = uiState.acceptedTerms,
                        onCheckedChange = { viewModel.onAcceptedTermsChange(it) },
                        colors = CheckboxDefaults.colors(checkedColor = NaranjaCercania)
                    )
                    Text(
                        text = "Acepto los ",
                        fontSize = 14.sp,
                        fontFamily = Nunito,
                        color = TextoSecundario
                    )
                    Text(
                        text = "Términos y Condiciones",
                        fontSize = 14.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        color = NaranjaCercania,
                        modifier = Modifier.clickable { showTerms = true }
                    )
                }

                if (showTerms) {
                    com.labajada.app.presentation.shared.legal.TermsAndConditionsDialog(
                        isSeller = false,
                        onDismiss = { showTerms = false },
                        onAccept = {
                            viewModel.onAcceptedTermsChange(true)
                            showTerms = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón sólido en Naranja Cercanía, sin degradado — consistente con el resto
                // de botones primarios de la app (flat, no gradientes).
                Button(
                    onClick = { viewModel.registerBuyer(onRegistrationComplete) },
                    enabled = uiState.name.isNotBlank() &&
                            isPhoneValid && uiState.phone.isNotBlank() &&
                            isEmailValid && uiState.email.isNotBlank() &&
                            passwordCheck.isValid && !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NaranjaCercania,
                        disabledContainerColor = NaranjaCercania.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Finalizar y Buscar Comida",
                            fontSize = 16.sp,
                            fontFamily = Baloo2,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Alerta flotante para manejo de errores globales
        AnimatedVisibility(
            visible = uiState.error != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = RojoAlerta)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = uiState.error ?: "",
                        color = RojoAlerta,
                        fontSize = 14.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}