package com.labajada.app.presentation.restaurant.dashboard.components.perfil

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.dashboard.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: RestaurantDashboardViewModel,
    onSwitchToBuyerMode: () -> Unit,
    onAccountDeactivated: () -> Unit,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isDualRole by viewModel.isDualRole.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
        PerfilInfoSection(
            viewModel = viewModel,
            isDualRole = isDualRole,
            onSwitchToBuyerMode = onSwitchToBuyerMode,
            onAccountDeactivated = onAccountDeactivated,
            onLogout = onLogout
        )
    }
}