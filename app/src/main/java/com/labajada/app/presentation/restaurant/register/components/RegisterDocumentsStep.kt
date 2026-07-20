package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.shared.others.ImageDimens
import com.labajada.app.presentation.shared.theme.*

@Composable
fun RegisterDocumentsStep(
    storePhotoUrl: String?,
    menuPhotoUrl: String?,
    permitPhotoUrl: String?,
    onStorePhotoSelected: (String?) -> Unit,
    onMenuPhotoSelected: (String?) -> Unit,
    onPermitPhotoSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Opcional...",
            fontSize = 27.sp,
            fontFamily = Bangers,
            color = MarronSazon
        )
        Text(
            text = "Esta información, puede agregarse después de crear tu cuenta:",
            fontSize = 14.sp,
            fontFamily = Nunito,
            color = TextoSecundarioRestaurante
        )

        Spacer(modifier = Modifier.height(4.dp))
        RegisterStepIndicator(currentStep = 3, totalSteps = 4)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "FOTO DE PORTADA:",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            Text(
                text = "Sube tu foto de portada esta es la que tus clientes va a ver",
                fontSize = 11.sp,
                fontFamily = Nunito,
                color = TextoSecundarioRestaurante
            )
            RestaurantImagePicker(
                label = "",
                imageUrl = storePhotoUrl,
                aspectRatioX = ImageDimens.RESTAURANT_COVER_RATIO_X,
                aspectRatioY = ImageDimens.RESTAURANT_COVER_RATIO_Y,
                onImageSelected = onStorePhotoSelected
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "FOTO DE CARTA O MENÚ:",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            Text(
                text = "Súbela para validarla en el sistema, te da la insignia de CONFIABLE",
                fontSize = 11.sp,
                fontFamily = Nunito,
                color = TextoSecundarioRestaurante
            )
            RestaurantImagePicker(
                label = "",
                imageUrl = menuPhotoUrl,
                aspectRatioX = ImageDimens.MENU_BOARD_RATIO_X,
                aspectRatioY = ImageDimens.MENU_BOARD_RATIO_Y,
                onImageSelected = onMenuPhotoSelected
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "PERMISO MUNICIPAL:",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            Text(
                text = "Sube tu permiso municipal y obtén la insignia de VERIFICADO...",
                fontSize = 11.sp,
                fontFamily = Nunito,
                color = TextoSecundarioRestaurante
            )
            RestaurantImagePicker(
                label = "",
                imageUrl = permitPhotoUrl,
                aspectRatioX = ImageDimens.PERMIT_RATIO_X,
                aspectRatioY = ImageDimens.PERMIT_RATIO_Y,
                onImageSelected = onPermitPhotoSelected
            )
        }
    }
}