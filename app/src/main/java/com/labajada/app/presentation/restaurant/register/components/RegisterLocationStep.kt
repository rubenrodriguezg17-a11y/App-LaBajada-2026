package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.shared.others.ImageDimens
import com.labajada.app.presentation.shared.theme.*

@Composable
fun RegisterLocationStep(
    addressDetails: String,
    isLocationSelected: Boolean,
    offersDelivery: Boolean,
    maxDeliveryDistanceKm: Double,
    imageUrl: String?,
    onAddressChange: (String) -> Unit,
    onOpenMapDialog: () -> Unit,
    onImageSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Excelente...",
            fontSize = 27.sp,
            fontFamily = Bangers,
            color = MarronSazon
        )
        Text(
            text = "Unas preguntitas más:",
            fontSize = 14.sp,
            fontFamily = Nunito,
            color = TextoSecundarioRestaurante
        )

        Spacer(modifier = Modifier.height(4.dp))
        RegisterStepIndicator(currentStep = 2, totalSteps = 4)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "¿DÓNDE ESTÁ TU LOCAL EN EL MAPA?, ¿HACE DELIVERY?",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(FondoCalidoRestaurante)
                    .border(
                        BorderStroke(1.dp, if (isLocationSelected) VerdeMatcha else DoradoTostado.copy(alpha = 0.6f)),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onOpenMapDialog() }
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (isLocationSelected) VerdeMatcha else DoradoTostado)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLocationSelected) {
                            if (offersDelivery) "Ubicación confirmada • Delivery hasta ${formatearDistancia((maxDeliveryDistanceKm * 1000).toFloat())}"
                            else "Ubicación confirmada • Solo recojo en local ✓"
                        } else {
                            "Toca aquí para elegir tu Ubicación en el Mapa"
                        },
                        fontSize = 14.sp,
                        fontFamily = Nunito,
                        color = TextoPrincipal,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "LA DIRECCIÓN DE TU LOCAL ¿ES?",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            OutlinedTextField(
                value = addressDetails,
                onValueChange = onAddressChange,
                placeholder = { Text("Ejemplo: Av Larco 123, a media cuadra del óvalo", fontFamily = Nunito) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (addressDetails.isNotBlank()) DoradoTostado else BordeCalidoRestaurante,
                    unfocusedBorderColor = if (addressDetails.isNotBlank()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "FOTO DE TU LOCAL:",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            Text(
                text = "Nota: Esta foto es privada, solo la veremos tú y nosotros",
                fontSize = 11.sp,
                fontFamily = Nunito,
                color = VerdeMatcha
            )
            RestaurantImagePicker(
                label = "",
                imageUrl = imageUrl,
                aspectRatioX = ImageDimens.STORE_PHOTO_RATIO_X,
                aspectRatioY = ImageDimens.STORE_PHOTO_RATIO_Y,
                onImageSelected = onImageSelected
            )
        }
    }
}