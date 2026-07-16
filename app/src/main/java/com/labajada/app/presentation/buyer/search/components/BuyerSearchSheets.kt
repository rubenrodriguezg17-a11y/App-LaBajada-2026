package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.EditarProfileSection
import com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.FavoritosProfileSection
import com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.HistorialProfileSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerSearchSheets(
    showProfileSheet: Boolean,
    onDismissProfile: () -> Unit,
    profileCurrentSection: String,
    onSectionChange: (String) -> Unit,
    searchViewModel: BuyerSearchViewModel,
    onSwitchToRestaurantMode: () -> Unit,
    onAccountDeactived: () -> Unit,
    onLogout: () -> Unit
) {
    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissProfile,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .navigationBarsPadding()
            ) {
                when (profileCurrentSection) {
                    "MENU" -> MenuProfileSection(
                        viewModel = searchViewModel,
                        onSectionChange = onSectionChange,
                        onDismissProfile = onDismissProfile,
                        onSwitchToRestaurantMode = onSwitchToRestaurantMode,
                        onAccountDeactivated = onAccountDeactived,
                        onLogout = onLogout
                    )
                    "FAVORITOS" -> FavoritosProfileSection(searchViewModel, onSectionChange)
                    "HISTORIAL" -> HistorialProfileSection(searchViewModel, onSectionChange)
                    "EDITAR" -> EditarProfileSection(viewModel = searchViewModel, onSectionChange = onSectionChange)
                    "CONFIG" -> com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile.BuyerConfigSection(
                        viewModel = searchViewModel,
                        onSectionChange = onSectionChange,
                        onAccountDeactivated = {
                            onDismissProfile()
                            onLogout()
                        }
                    )
                }
            }
        }
    }
}