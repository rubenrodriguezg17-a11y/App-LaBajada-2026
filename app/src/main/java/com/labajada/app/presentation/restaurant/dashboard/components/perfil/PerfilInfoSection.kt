package com.labajada.app.presentation.restaurant.dashboard.components.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.ui.helpers.calcularNivelInsignia
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel

/**
 * Pantalla de perfil del restaurante: portada + datos editables + documentos +
 * configuración de cuenta + logout.
 *
 * Este archivo es solo el ORQUESTADOR. Cada sección vive en su propio archivo
 * (antes todo esto era un único composable de ~550 líneas):
 *  - PerfilCoverHeader.kt       → foto de portada, nombre, insignia
 *  - PerfilEditableInfoCard.kt  → campos editables (nombre, doc, contacto, rubro, GPS, delivery)
 *  - PerfilDocumentsCard.kt     → documentos de verificación
 *  - PerfilConfigSection.kt     → configuración de cuenta / rol (ya existía separado)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilInfoSection(
    viewModel: RestaurantDashboardViewModel,
    isDualRole: Boolean,
    onSwitchToBuyerMode: () -> Unit,
    onAccountDeactivated: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val isGoogleUser by viewModel.isGoogleUser.collectAsState()

    val nivelInsignia = calcularNivelInsignia(
        documentType = state.resDocumentType,
        isVerified = state.resIsVerified,
        documentsSubmittedAt = state.resDocumentsSubmittedAt,
        storePhotoUrl = state.resStorePhotoUrl,
        menuPhotoUrl = state.resMenuPhotoUrl,
        permitPhotoUrl = state.resPermitPhotoUrl
    )

    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        PerfilCoverHeader(viewModel = viewModel, state = state, nivelInsignia = nivelInsignia)

        PerfilEditableInfoCard(viewModel = viewModel, state = state)

        Spacer(modifier = Modifier.height(4.dp))

        PerfilDocumentsCard(viewModel = viewModel, state = state)

        Spacer(modifier = Modifier.height(4.dp))

        PerfilConfigSection(
            viewModel = viewModel,
            isDualRole = isDualRole,
            isGoogleUser = isGoogleUser,
            onSwitchToBuyerMode = onSwitchToBuyerMode,
            onAccountDeactivated = onAccountDeactivated
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cerrar Sesión del Local", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
