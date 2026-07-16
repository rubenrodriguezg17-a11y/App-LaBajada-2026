package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.EditarProfileSection
import com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.FavoritosProfileSection
import com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.HistorialProfileSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerPerfilTab(
    searchViewModel: BuyerSearchViewModel,
    onSwitchToRestaurantMode: () -> Unit,
    onAccountDeactivated: () -> Unit,
    onLogout: () -> Unit
) {
    var currentSection by remember { mutableStateOf("MENU") }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(scrollState)) {
        when (currentSection) {
            "MENU" -> MenuProfileSection(
                viewModel = searchViewModel,
                onSectionChange = { currentSection = it },
                onDismissProfile = {},
                onSwitchToRestaurantMode = onSwitchToRestaurantMode,
                onAccountDeactivated = onAccountDeactivated,
                onLogout = onLogout
            )
            "FAVORITOS" -> FavoritosProfileSection(searchViewModel) { currentSection = it }
            "HISTORIAL" -> HistorialProfileSection(searchViewModel) { currentSection = it }
            "EDITAR" -> EditarProfileSection(viewModel = searchViewModel, onSectionChange = { currentSection = it })
        }
    }
}