package com.labajada.app.presentation.login.verification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.shared.theme.*

/**
 * Pantalla que bloquea el paso a Home hasta que el usuario confirme su correo.
 * A esta pantalla solo llegan cuentas de correo/contraseña sin verificar (login o
 * recién registradas); las cuentas de Google nunca pasan por aquí porque Firebase
 * ya las marca como verificadas.
 */
@Composable
fun EmailVerificationScreen(
    viewModel: EmailVerificationViewModel,
    onVerified: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IvoryBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
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
                text = "Verifica tu correo",
                fontSize = 28.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Black,
                color = TextoPrincipal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Te enviamos un enlace de confirmación a ${state.email}. Tócalo para activar tu cuenta y poder continuar.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                color = TextoSecundario,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.checkVerification(onVerified) },
                enabled = !state.isCheckingVerification,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CharcoalBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (state.isCheckingVerification) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Icon(Icons.Filled.MarkEmailRead, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ya verifiqué, continuar", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { viewModel.resendEmail() },
                enabled = !state.isResending && state.resendCooldownSeconds == 0
            ) {
                Text(
                    text = when {
                        state.isResending -> "Enviando..."
                        state.resendCooldownSeconds > 0 -> "Reenviar correo (${state.resendCooldownSeconds}s)"
                        else -> "Reenviar correo"
                    },
                    color = RojoGochujang,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = { viewModel.logout(onLoggedOut) }) {
                Text("Usar otra cuenta / Cerrar sesión", color = TextoSecundario, fontSize = 13.sp)
            }
        }

        AnimatedVisibility(
            visible = state.errorMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = RojoGochujang)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.errorMessage ?: "",
                        color = Color(0xFFB71C1C),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = state.infoMessage != null && state.errorMessage == null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = VerdeMatcha.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MarkEmailRead, contentDescription = null, tint = VerdeMatcha)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.infoMessage ?: "",
                        color = TextoPrincipal,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
