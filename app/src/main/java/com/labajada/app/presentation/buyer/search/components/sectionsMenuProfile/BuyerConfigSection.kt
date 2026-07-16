package com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun BuyerConfigSection(
    viewModel: BuyerSearchViewModel,
    onSectionChange: (String) -> Unit,
    onAccountDeactivated: () -> Unit
) {
    var showChangePassword by remember { mutableStateOf(false) }
    var showChangeEmail by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    var showSupport by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onSectionChange("MENU") }.padding(vertical = 4.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }

        Text("Configuración", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))

        Text("CUENTA Y SEGURIDAD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                BuyerConfigRow(Icons.Default.Lock, "Cambiar contraseña") { showChangePassword = true }
                HorizontalDivider(color = Color(0xFFF5F5F5))
                BuyerConfigRow(Icons.Default.Email, "Cambiar correo electrónico") { showChangeEmail = true }
                HorizontalDivider(color = Color(0xFFF5F5F5))
                BuyerConfigRow(Icons.Default.DeleteForever, "Desactivar cuenta", titleColor = Color(0xFFD32F2F)) { showDeleteAccount = true }
            }
        }

        Text("LEGAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            BuyerConfigRow(Icons.Default.Description, "Términos y Condiciones") { showTerms = true }
        }

        Text("SOPORTE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9E9E9E))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            BuyerConfigRow(Icons.Default.HelpOutline, "Ayuda y preguntas frecuentes") { showSupport = true }
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
private fun BuyerConfigRow(
    icon: ImageVector,
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