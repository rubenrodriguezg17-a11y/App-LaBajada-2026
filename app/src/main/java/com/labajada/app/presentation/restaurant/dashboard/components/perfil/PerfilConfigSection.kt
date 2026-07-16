package com.labajada.app.presentation.restaurant.dashboard.components.perfil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.dashboard.*
import com.labajada.app.presentation.shared.account.ChangeEmailDialog
import com.labajada.app.presentation.shared.account.ChangePasswordDialog
import com.labajada.app.presentation.shared.account.DeleteAccountDialog

@Composable
fun PerfilConfigSection(
    viewModel: RestaurantDashboardViewModel,
    isDualRole: Boolean,
    isGoogleUser: Boolean,
    onSwitchToBuyerMode: () -> Unit,
    onAccountDeactivated: () -> Unit
) {
    var showChangePassword by remember { mutableStateOf(false) }
    var showChangeEmail by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    var showSoporte by remember { mutableStateOf(false) }
    var showTermsReadOnly by remember { mutableStateOf(false) }


    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

        Text(
            text = "Configuración",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF263238)
        )
        if (isDualRole) {
            Text(
                text = "MODO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9E9E9E)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                ConfigOptionRow(
                    icon = Icons.Default.SwapHoriz,
                    title = "Cambiar a modo Comprador",
                    onClick = onSwitchToBuyerMode
                )
            }
        }

        // --- Cuenta y Seguridad: solo para quienes entraron con correo/contraseña ---
        if (!isGoogleUser) {
            Text(
                text = "CUENTA Y SEGURIDAD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9E9E9E)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    ConfigOptionRow(
                        icon = Icons.Default.Lock,
                        title = "Cambiar contraseña",
                        onClick = { showChangePassword = true }
                    )
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    ConfigOptionRow(
                        icon = Icons.Default.Email,
                        title = "Cambiar correo electrónico",
                        onClick = { showChangeEmail = true }
                    )
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    ConfigOptionRow(
                        icon = Icons.Default.DeleteForever,
                        title = "Eliminar cuenta",
                        titleColor = Color(0xFFD32F2F),
                        onClick = { showDeleteAccount = true }
                    )
                }
            }
        } else {
            Text(
                text = "CUENTA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9E9E9E)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Tu cuenta está vinculada a Google. Para cambiar tu contraseña, correo o eliminar tu cuenta, hazlo desde la configuración de tu cuenta de Google.",
                    fontSize = 12.sp,
                    color = Color(0xFF616161),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // --- Soporte ---
        Text(
            text = "SOPORTE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9E9E9E)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            ConfigOptionRow(
                icon = Icons.Default.HelpOutline,
                title = "Ayuda y preguntas frecuentes",
                onClick = { showSoporte = true }
            )
            ConfigOptionRow(
                icon = Icons.Default.Description,
                title = "Términos y Condiciones",
                onClick = { showTermsReadOnly = true }
            )
        }
    }

    if (showChangePassword) {
        ChangePasswordDialog(
            userRepository = viewModel.userRepository,
            onDismiss = { showChangePassword = false },
            onSuccess = { showChangePassword = false }
        )
    }
    if (showChangeEmail) {
        ChangeEmailDialog (
            userRepository = viewModel.userRepository,
            onDismiss = { showChangeEmail = false },
            onSuccess = { showChangeEmail = false }
        )
    }
    if (showDeleteAccount) {
        DeleteAccountDialog(
            onConfirmDeactivate = { password ->
                viewModel.deactivateAccountAndRestaurant(password)
            },
            onDismiss = { showDeleteAccount = false },
            onAccountDeactivated = {
                showDeleteAccount = false
                onAccountDeactivated()
            }
        )
    }

    if (showSoporte) {
        AlertDialog(
            onDismissRequest = { showSoporte = false },
            title = { Text("Ayuda") },
            text = { Text("Próximamente disponible.") },
            confirmButton = {
                TextButton(onClick = { showSoporte = false }) { Text("Cerrar") }
            }
        )
    }
    if (showTermsReadOnly) {
        com.labajada.app.presentation.shared.legal.TermsAndConditionsDialog(
            isSeller = true,
            onDismiss = { showTermsReadOnly = false }
        )
    }
}

@Composable
private fun ConfigOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    titleColor: Color = Color(0xFF212121),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF757575), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontSize = 14.sp, color = titleColor, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFBDBDBD))
    }
}