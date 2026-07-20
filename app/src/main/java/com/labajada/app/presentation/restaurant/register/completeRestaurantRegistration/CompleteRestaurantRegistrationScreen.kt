package com.labajada.app.presentation.restaurant.register.completeRestaurantRegistration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.restaurant.register.components.DeliveryRadiusMapDialog
import com.labajada.app.presentation.restaurant.register.components.RegisterBusinessInfoStep
import com.labajada.app.presentation.restaurant.register.components.RegisterDocumentsStep
import com.labajada.app.presentation.restaurant.register.components.RegisterLocationStep
import com.labajada.app.presentation.shared.legal.TermsAndConditionsDialog

@Composable
fun CompleteRestaurantRegistrationScreen(
    viewModel: CompleteRestaurantRegistrationViewModel,
    onComplete: () -> Unit,
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showExitConfirm by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (state.currentStep > 1) {
            viewModel.previousStep()
        } else {
            showExitConfirm = true
        }
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = {
                Text("Quieres salir del registro", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Puedes terminar de activar tu restaurante más tarde. Por ahora, elige cómo quieres continuar.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showExitConfirm = false
                    onExit()
                }) {
                    Text("Salir por ahora", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text("Seguir llenando")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
            when (state.currentStep) {
                1 -> RegisterBusinessInfoStep(
                    restaurantName = state.restaurantName,
                    documentType = state.documentType,
                    documentNumber = state.documentNumber,
                    phoneNumber = state.phoneNumber,
                    selectedCategory = state.selectedCategory,
                    expandedCategory = state.expandedCategory,
                    businessHours = state.businessHours,
                    onNameChange = viewModel::onNameChange,
                    onDocumentTypeChange = viewModel::onDocumentTypeChange,
                    onDocumentNumberChange = viewModel::onDocumentNumberChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onCategorySelected = viewModel::onCategorySelected,
                    onToggleCategoryDropdown = viewModel::toggleCategoryDropdown,
                    onBusinessHoursChange = viewModel::onBusinessHoursChange
                )
                2 -> RegisterLocationStep(
                    addressDetails = state.addressDetails,
                    isLocationSelected = state.isLocationSelected,
                    offersDelivery = state.offersDelivery,
                    maxDeliveryDistanceKm = state.maxDeliveryDistanceKm,
                    imageUrl = state.imageUrl,
                    onAddressChange = viewModel::onAddressChange,
                    onOpenMapDialog = { viewModel.toggleMapDialog(true) },
                    onImageSelected = viewModel::onImageSelected
                )
                3 -> RegisterDocumentsStep(
                    storePhotoUrl = state.storePhotoUrl,
                    menuPhotoUrl = state.menuPhotoUrl,
                    permitPhotoUrl = state.permitPhotoUrl,
                    onStorePhotoSelected = viewModel::onStorePhotoSelected,
                    onMenuPhotoSelected = viewModel::onMenuPhotoSelected,
                    onPermitPhotoSelected = viewModel::onPermitPhotoSelected
                )
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(state.error ?: "", color = Color(0xFFD32F2F), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.currentStep > 1) {
                OutlinedButton(
                    onClick = { viewModel.previousStep() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Atrás", fontWeight = FontWeight.Bold)
                }
            }
            Button(
                onClick = {
                    when (state.currentStep) {
                        3 -> showTermsDialog = true
                        else -> viewModel.nextStep()
                    }
                },
                modifier = Modifier.weight(2f).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4332)),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    val label = when (state.currentStep) {
                        2 -> "Continuar"
                        3 -> "Omitir"
                        else -> "Siguiente"
                    }
                    Text(text = label, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (state.showMapDialog) {
        DeliveryRadiusMapDialog(
            initialLatitude = state.latitude,
            initialLongitude = state.longitude,
            initialOffersDelivery = state.offersDelivery,
            initialMaxDeliveryDistanceKm = state.maxDeliveryDistanceKm,
            onDismiss = { viewModel.toggleMapDialog(false) },
            onConfirm = { lat, lng, offersDelivery, radiusKm ->
                viewModel.onLocationConfirmed(lat, lng)
                viewModel.onOffersDeliveryChange(offersDelivery)
                viewModel.onMaxDeliveryDistanceChange(radiusKm)
                viewModel.toggleMapDialog(false)
            }
        )
    }

    if (showTermsDialog) {
        TermsAndConditionsDialog(
            isSeller = true,
            onDismiss = { showTermsDialog = false },
            onAccept = {
                showTermsDialog = false
                viewModel.completeRegistration(onComplete)
            }
        )
    }
}