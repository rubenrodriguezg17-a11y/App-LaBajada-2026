package com.labajada.app.presentation.restaurant.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import com.labajada.app.presentation.restaurant.register.components.RegisterCredentialsStep
import com.labajada.app.presentation.restaurant.register.components.RegisterDocumentsStep
import com.labajada.app.presentation.restaurant.register.components.RegisterLocationStep
import com.labajada.app.presentation.restaurant.register.components.RegisterStepIndicator
import com.labajada.app.presentation.shared.legal.TermsAndConditionsDialog
import com.labajada.app.presentation.shared.theme.*

@Composable
fun RestaurantRegisterScreen(
    viewModel: RestaurantRegisterViewModel,
    onRegistrationComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showTermsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            AnimatedContent(
                targetState = state.currentStep,
                label = "register_step_transition",
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { step ->
                when (step) {
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
                    4 -> RegisterCredentialsStep(
                        ownerFullName = state.ownerFullName,
                        email = state.email,
                        password = state.password,
                        confirmPassword = state.confirmPassword,
                        onOwnerFullNameChange = viewModel::onOwnerFullNameChange,
                        onEmailChange = viewModel::onEmailChange,
                        onPasswordChange = viewModel::onPasswordChange,
                        onConfirmPasswordChange = viewModel::onConfirmPasswordChange
                    )
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.error ?: "",
                    color = RojoAlerta,
                    fontSize = 13.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.currentStep > 1) {
                OutlinedButton(
                    onClick = { viewModel.previousStep() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MarronSazon),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeCalidoRestaurante)
                ) {
                    Text("Atrás", fontFamily = Baloo2, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = {
                    when (state.currentStep) {
                        4 -> showTermsDialog = true
                        else -> viewModel.nextStep()
                    }
                },
                modifier = Modifier.weight(2f).height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DoradoTostado,
                    disabledContainerColor = DoradoTostado.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MarronSazon, modifier = Modifier.size(24.dp))
                } else {
                    val label = when (state.currentStep) {
                        2 -> "Continuar"
                        3 -> "Omitir"
                        4 -> "Terminar"
                        else -> "Siguiente"
                    }
                    Text(text = label, fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MarronSazon)
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
                viewModel.registerRestaurant(onRegistrationComplete)
            }
        )
    }
}