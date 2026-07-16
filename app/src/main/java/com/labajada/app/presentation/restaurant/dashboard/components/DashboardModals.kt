package com.labajada.app.presentation.restaurant.dashboard.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.dashboard.*
import com.labajada.app.presentation.restaurant.dashboard.components.modals.DeleteDishDialog
import com.labajada.app.presentation.restaurant.dashboard.components.modals.DishFormBottomSheet
import com.labajada.app.presentation.restaurant.dashboard.components.modals.RestaurantMapDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardModals(
    viewModel: RestaurantDashboardViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state by viewModel.uiState.collectAsState()

    // --- 1. Diálogo de confirmación para eliminar ---
    if (state.showDeleteDialog) {
        DeleteDishDialog(
            itemId = state.itemIdToEdit,
            onConfirm = { id ->
                viewModel.eliminarPlatillo(id)
                viewModel.toggleDeleteDialog(false)
            },
            onDismiss = { viewModel.toggleDeleteDialog(false) }
        )
    }

    // --- 2. Hoja inferior del Formulario de Platos ---
    if (state.showFormSheet) {
        DishFormBottomSheet (
            viewModel = viewModel,
            sheetState = sheetState,
            onSave = {
                // guardarPlatillo valida nombre/precio; si el formulario es inválido no hace
                // nada y devuelve false, así que la hoja se queda abierta con el mensaje de
                // error visible en vez de cerrarse como si el platillo se hubiera guardado.
                val guardado = viewModel.guardarPlatillo(context)
                if (guardado) {
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            viewModel.toggleFormSheet(false)
                            viewModel.prepararNuevoPlatillo()
                        }
                    }
                }
            },
            onDismiss = {
                viewModel.toggleFormSheet(false)
                viewModel.prepararNuevoPlatillo()
            }
        )
    }

    if (state.showProfileMapDialog) {
        RestaurantMapDialog(
            initialLatitude = state.resLatitude,
            initialLongitude = state.resLongitude,
            onConfirm = { lat, lng ->
                viewModel.onProfileLocationConfirmed(lat, lng)
            },
            onDismiss = {
                viewModel.toggleProfileMap(false)
            }
        )
    }
}
