package com.labajada.app.presentation.restaurant.dashboard.components.perfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage
import com.labajada.app.core.ui.helpers.RestaurantBadgeLevel
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardState
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.dashboard.onProfileImageCropped
import com.labajada.app.presentation.shared.others.ImageDimens

/**
 * Foto de portada del local (tocar para cambiarla), con el nombre del local y la
 * insignia de verificado superpuestos. Extraído de PerfilInfoSection para que ese
 * archivo no siga creciendo.
 */
@Composable
fun PerfilCoverHeader(
    viewModel: RestaurantDashboardViewModel,
    state: RestaurantDashboardState,
    nivelInsignia: RestaurantBadgeLevel
) {
    val context = LocalContext.current

    var pendingCropUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) pendingCropUri = uri
    }

    pendingCropUri?.let { uri ->
        com.labajada.app.presentation.shared.cropper.ImageCropperDialog(
            imageUri = uri,
            aspectRatio = ImageDimens.RESTAURANT_COVER_RATIO,
            onCancel = { pendingCropUri = null },
            onCropped = { bitmap ->
                viewModel.onProfileImageCropped(context, bitmap)
                pendingCropUri = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ImageDimens.RESTAURANT_COVER_RATIO)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF263238))
            .clickable { imagePickerLauncher.launch("image/*") }
    ) {
        if (!state.resImageUrl.isNullOrBlank()) {
            AsyncImage(
                model = state.resImageUrl,
                contentDescription = "Foto de portada",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().
                aspectRatio(ImageDimens.RESTAURANT_COVER_RATIO)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Cambiar portada", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.resNameByOwner.ifBlank { "Mi Huarique" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            if (nivelInsignia != RestaurantBadgeLevel.NINGUNO) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = nivelInsignia.etiqueta,
                    tint = nivelInsignia.color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
