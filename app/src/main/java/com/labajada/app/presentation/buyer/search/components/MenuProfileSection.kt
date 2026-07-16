package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.shared.account.ChangeEmailDialog
import com.labajada.app.presentation.shared.account.ChangePasswordDialog
import com.labajada.app.presentation.shared.account.DeleteAccountDialog
import com.labajada.app.presentation.shared.legal.TermsAndConditionsDialog
import com.labajada.app.presentation.shared.support.SupportDialog

@Composable
fun MenuProfileSection(
    viewModel: BuyerSearchViewModel,
    onSectionChange: (String) -> Unit,
    onDismissProfile: () -> Unit,
    onSwitchToRestaurantMode: () -> Unit,
    onAccountDeactivated: () -> Unit,
    onLogout: () -> Unit
) {
    val userName by viewModel.currentBuyerName.collectAsState()
    val userEmail by viewModel.currentBuyerEmail.collectAsState()
    val isDualRole by viewModel.isDualRole.collectAsState()
    val isGoogleUser by viewModel.isGoogleUser.collectAsState()

    var showChangePassword by remember { mutableStateOf(false) }
    var showChangeEmail by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    var showSupport by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // --- Header: avatar + nombre ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFC2C2C2), shape = RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = Color(0xFF212121)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = userName.ifBlank { "Comensal" },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF212121)
                )
                Text(
                    text = userEmail,
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
            }
        }

        // --- Favoritos / Historial / Editar perfil: 3 tarjetas ---
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProfileShortcutCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.FavoriteBorder,
                iconTint = Color(0xFFD32F2F),
                title = "Favoritos",
                subtitle = "Mis Favoritos",
                onClick = { onSectionChange("FAVORITOS") }
            )
            ProfileShortcutCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Refresh,
                iconTint = Color(0xFFD32F2F),
                title = "Historial",
                subtitle = "Historial de Pedidos",
                onClick = { onSectionChange("HISTORIAL") }
            )
            ProfileShortcutCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Edit,
                iconTint = Color(0xFFD32F2F),
                title = "Editar perfil",
                subtitle = "Editar información personal",
                onClick = { onSectionChange("EDITAR") }
            )
        }

        // --- Configuración (siempre visible, como en el lado restaurante) ---
        Text("Configuración", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))

        if (!isGoogleUser) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    BuyerConfigRow(Icons.Default.Email, "Cambiar correo", userEmail) { showChangeEmail = true }
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    BuyerConfigRow(Icons.Default.Lock, "Cambiar contraseña", "••••••••") { showChangePassword = true }
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    BuyerConfigRow(
                        icon = Icons.Default.Cancel,
                        title = "Desactivar cuenta",
                        subtitle = "Pausar o eliminar",
                        titleColor = Color(0xFFD32F2F),
                        onClick = { showDeleteAccount = true }
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Tu cuenta está vinculada a Google. Para cambiar tu contraseña, correo o eliminar tu cuenta, hazlo desde la configuración de tu cuenta de Google.",
                    fontSize = 12.sp,
                    color = Color(0xFF616161),
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        // --- Legal ---
        Text("LEGAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            BuyerConfigRow(Icons.Default.Description, "Términos y Condiciones", null) { showTerms = true }
        }

        // --- Soporte ---
        Text("SOPORTE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            BuyerConfigRow(Icons.AutoMirrored.Filled.HelpOutline, "Ayuda y preguntas frecuentes", null) { showSupport = true }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            if (isDualRole) {
                BuyerConfigRow(Icons.Default.SwapHoriz, "Cambiar a modo Restaurante", null) {
                    onDismissProfile()
                    onSwitchToRestaurantMode()
                }
            } else {
                BuyerConfigRow(
                    icon = Icons.Default.Storefront,
                    title = "Agregar negocio",
                    subtitle = "Registra tu restaurante y empieza a vender"
                ) {
                    onDismissProfile()
                    onSwitchToRestaurantMode()
                }
            }
        }

        Button(
            onClick = {
                onDismissProfile()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cerrar Sesión", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showChangePassword) {
        ChangePasswordDialog(
            userRepository = viewModel.userRepository,
            onDismiss = { showChangePassword = false },
            onSuccess = { showChangePassword = false }
        )
    }
    if (showChangeEmail) {
        ChangeEmailDialog(
            userRepository = viewModel.userRepository,
            onDismiss = { showChangeEmail = false },
            onSuccess = { showChangeEmail = false }
        )
    }
    if (showDeleteAccount) {
        DeleteAccountDialog(
            onConfirmDeactivate = { password -> viewModel.userRepository.deactivateAccount(password) },
            onDismiss = { showDeleteAccount = false },
            onAccountDeactivated = {
                showDeleteAccount = false
                onAccountDeactivated()
            }
        )
    }
    if (showTerms) {
        TermsAndConditionsDialog(isSeller = false, onDismiss = { showTerms = false })
    }
    if (showSupport) {
        SupportDialog(isSeller = false, onDismiss = { showSupport = false })
    }
}

@Composable
private fun ProfileShortcutCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
            Text(
                subtitle,
                fontSize = 10.sp,
                color = Color(0xFF9E9E9E),
                maxLines = 1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun BuyerConfigRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
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
            Column {
                Text(title, fontSize = 14.sp, color = titleColor, fontWeight = FontWeight.Medium)
                if (subtitle != null) {
                    Text(subtitle, fontSize = 11.sp, color = Color(0xFF9E9E9E))
                }
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFBDBDBD))
    }
}