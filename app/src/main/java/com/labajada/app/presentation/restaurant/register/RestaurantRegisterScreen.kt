package com.labajada.app.presentation.restaurant.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

@Composable
fun RestaurantRegisterScreen(
    viewModel: RestaurantRegisterViewModel,
    onRegistrationComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Registra tu Huarique",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF263238),
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )
        Text(
            text = "Únete a La Bajada y gestiona tus pedidos al toque",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        RegisterStepIndicator(currentStep = state.currentStep, totalSteps = 4)

        Spacer(modifier = Modifier.height(20.dp))

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
                        acceptedTerms = state.acceptedTerms,
                        onOwnerFullNameChange = viewModel::onOwnerFullNameChange,
                        onEmailChange = viewModel::onEmailChange,
                        onPasswordChange = viewModel::onPasswordChange,
                        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                        onAcceptedTermsChange = viewModel::onAcceptedTermsChange
                    )
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.error ?: "",
                    color = Color(0xFFD32F2F),
                    fontSize = 13.sp,
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
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Atrás", fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = {
                    if (state.currentStep < 4) {
                        viewModel.nextStep()
                    } else {
                        viewModel.registerRestaurant(onRegistrationComplete)
                    }
                },
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF263238),
                    disabledContainerColor = Color(0xFFCFD8DC)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (state.currentStep < 4) "Siguiente" else "Registrar Comercio",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
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