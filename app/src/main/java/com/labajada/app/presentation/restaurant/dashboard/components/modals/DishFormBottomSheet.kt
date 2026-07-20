package com.labajada.app.presentation.restaurant.dashboard.components.modals

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.dashboard.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishFormBottomSheet(
    viewModel: RestaurantDashboardViewModel,
    sheetState: SheetState,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        DishFormContent(
            viewModel = viewModel,
            onSave = onSave
        )
    }
}

@Composable
private fun DishFormContent(
    viewModel: RestaurantDashboardViewModel,
    onSave: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val croppedUri = result.data?.let { com.yalantis.ucrop.UCrop.getOutput(it) }
            viewModel.onDishImageChange(croppedUri)
        }
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val destinationFile = java.io.File(context.cacheDir, "plato_recortado_${System.currentTimeMillis()}.jpg")
            val destinationUri = androidx.core.content.FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", destinationFile
            )
            val intent = com.yalantis.ucrop.UCrop.of(uri, destinationUri)
                .withAspectRatio(
                    com.labajada.app.presentation.shared.others.ImageDimens.DISH_PHOTO_RATIO_X,
                    com.labajada.app.presentation.shared.others.ImageDimens.DISH_PHOTO_RATIO_Y
                )
                .withMaxResultSize(1200, 1200)
                .getIntent(context)
            cropLauncher.launch(intent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (state.isEditing) "Editar Platillo" else "Nuevo Platillo Gastronómico",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0F0F0))
                .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(12.dp))
                .clickable {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (state.selectedImageUri != null) {
                AsyncImage(
                    model = state.selectedImageUri,
                    contentDescription = "Imagen del plato",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar imagen",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Toca para añadir foto", color = Color.Gray)
                }
            }
        }

        OutlinedTextField(
            value = state.dishName,
            onValueChange = { viewModel.onDishNameChange(it) },
            label = { Text("Nombre del plato") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = state.dishPrice,
            onValueChange = { nuevoValor ->
                if (nuevoValor.matches(Regex("^\\d{0,5}(\\.\\d{0,2})?$"))) {
                    viewModel.onDishPriceChange(nuevoValor)
                }
            },
            label = { Text("Precio (S/.)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        val isFormValid = viewModel.isDishFormValid(state)
        if (!isFormValid && (state.dishName.isNotEmpty() || state.dishPrice.isNotEmpty())) {
            Text(
                text = "Ingresa un nombre y un precio mayor a S/. 0.",
                color = Color(0xFFD32F2F),
                fontSize = 12.sp
            )
        }

        Button(
            onClick = onSave,
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (state.isEditing) "Guardar Cambios" else "Agregar al Menú",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
