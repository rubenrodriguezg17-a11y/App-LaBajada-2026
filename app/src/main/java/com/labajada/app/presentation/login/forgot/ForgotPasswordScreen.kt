package com.labajada.app.presentation.login.forgot

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.shared.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val isEmailValid = remember(state.email) {
        android.util.Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IvoryBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextoPrincipal,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Crossfade(
                targetState = state.emailSent,
                label = "ForgotScreenTransition"
            ) { emailSent ->
                if (!emailSent) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "Recupera tu cuenta",
                            fontSize = 32.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Black,
                            color = TextoPrincipal,
                            letterSpacing = (-0.5).sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Ingresa el correo con el que te registraste y te enviaremos un enlace para crear una nueva contraseña.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light,
                            color = TextoSecundario,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            label = { Text("Correo Electrónico", fontWeight = FontWeight.Medium) },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.MailOutline,
                                    null,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            enabled = !state.isLoading,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF2F0EB),
                                unfocusedContainerColor = Color(0xFFF2F0EB),
                                focusedBorderColor = if (isEmailValid) VerdeMatcha else RojoGochujang,
                                unfocusedBorderColor = if (isEmailValid) VerdeMatcha.copy(alpha = 0.5f) else BordeSuave,
                                cursorColor = RojoGochujang,
                                focusedLabelColor = TextoPrincipal
                            )
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = { viewModel.sendResetEmail() },
                            enabled = state.email.isNotBlank() && isEmailValid && !state.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(RojoGochujang, Color(0xFFE65100))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    "Enviar enlace de recuperación",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = RoundedCornerShape(32.dp),
                            color = VerdeMatcha.copy(alpha = 0.08f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.MailOutline,
                                    contentDescription = null,
                                    tint = VerdeMatcha,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "¡Revisa tu correo!",
                            fontSize = 28.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Black,
                            color = TextoPrincipal,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Te enviamos un enlace a ${state.email} para que crees una nueva contraseña.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light,
                            color = TextoSecundario,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        Button(
                            onClick = onBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CharcoalBackground),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Volver al inicio de sesión",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.error != null && !state.emailSent,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = RojoGochujang)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.error ?: "",
                        color = Color(0xFFB71C1C),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}