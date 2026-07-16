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
import com.labajada.app.presentation.restaurant.register.components.TermsAcceptanceRow

@Composable
fun CompleteRestaurantRegistrationScreen(
    viewModel: CompleteRestaurantRegistrationViewModel,
    onComplete: () -> Unit,
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showExitConfirm by remember { mutableStateOf(false) }

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
        Text(
            text = "¡Ya casi terminamos!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF263238),
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )
        Text(
            text = "Cuéntanos sobre tu negocio para activarlo en La Bajada",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
            when (state.currentStep) {
                1 -> RegisterBusinessInfoStep(
                    restaurantName = state.restaurantName,
                    documentType = state.documentType,
                    documentNumber = state.documentNumber,
                    phoneNumber = state.phoneNumber,
                    selectedCategory = state.selectedCategory,
                    expandedCategory = state.expandedCategory,
                    onNameChange = viewModel::onNameChange,
                    onDocumentTypeChange = viewModel::onDocumentTypeChange,
                    onDocumentNumberChange = viewModel::onDocumentNumberChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onCategorySelected = viewModel::onCategorySelected,
                    onToggleCategoryDropdown = viewModel::toggleCategoryDropdown
                )
                2 -> RegisterLocationStep(
                    addressDetails = state.addressDetails,
                    isLocationSelected = state.isLocationSelected,
                    offersDelivery = state.offersDelivery,
                    maxDeliveryDistanceKm = state.maxDeliveryDistanceKm,
                    imageUrl = state.imageUrl,
                    businessHours = state.businessHours,
                    onAddressChange = viewModel::onAddressChange,
                    onOpenMapDialog = { viewModel.toggleMapDialog(true) },
                    onImageSelected = viewModel::onImageSelected,
                    onBusinessHoursChange = viewModel::onBusinessHoursChange
                )
                3 -> Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    RegisterDocumentsStep(
                        storePhotoUrl = state.storePhotoUrl,
                        menuPhotoUrl = state.menuPhotoUrl,
                        permitPhotoUrl = state.permitPhotoUrl,
                        onStorePhotoSelected = viewModel::onStorePhotoSelected,
                        onMenuPhotoSelected = viewModel::onMenuPhotoSelected,
                        onPermitPhotoSelected = viewModel::onPermitPhotoSelected
                    )
                    TermsAcceptanceRow(
                        acceptedTerms = state.acceptedTerms,
                        onAcceptedTermsChange = viewModel::onAcceptedTermsChange
                    )
                }
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
                    if (state.currentStep < 3) viewModel.nextStep()
                    else viewModel.completeRegistration(onComplete)
                },
                modifier = Modifier.weight(2f).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (state.currentStep < 3) "Siguiente" else "Activar mi negocio", fontWeight = FontWeight.Bold)
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
}