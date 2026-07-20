package com.labajada.app.presentation.restaurant.register.components

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.labajada.app.presentation.shared.theme.Bangers
import com.yalantis.ucrop.UCrop
import java.io.File

@Composable
fun RestaurantImagePicker(
    label: String = "Foto de portada (opcional)",
    subtitle: String? = null,
    imageUrl: String?,
    enabled: Boolean = true,
    confirmChangeMessage: String? = null,
    aspectRatioX: Float = 4f,
    aspectRatioY: Float = 3f,
    onImageSelected: (String?) -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri = result.data?.let { UCrop.getOutput(it) }
            if (croppedUri != null) onImageSelected(croppedUri.toString())
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val error = result.data?.let { UCrop.getError(it) }
            android.util.Log.e("RestaurantImagePicker", "Error al recortar imagen", error)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val destinationFile = File(context.cacheDir, "doc_recortado_${System.currentTimeMillis()}.jpg")
            val destinationUri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", destinationFile
            )
            val intent = UCrop.of(uri, destinationUri)
                .withAspectRatio(aspectRatioX, aspectRatioY)
                .withMaxResultSize(1600, 1600)
                .getIntent(context)
            cropLauncher.launch(intent)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontFamily = Bangers,
            color = Color(0xFF263238)
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatioX / aspectRatioY)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
                .clickable(enabled = enabled) {
                    if (confirmChangeMessage != null && imageUrl != null) {
                        showConfirm = true
                    } else {
                        launcher.launch("image/*")
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (!enabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloqueado",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "En revisión",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Agregar foto",
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Toca para subir una foto",
                        fontSize = 13.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }
        }

        if (imageUrl != null && enabled) {
            Text(
                text = "Quitar foto",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                modifier = Modifier.clickable {
                    if (confirmChangeMessage != null) showConfirm = true
                    else onImageSelected(null)
                }
            )
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("¿Cambiar este documento?", fontWeight = FontWeight.Bold) },
            text = { Text(confirmChangeMessage ?: "") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    launcher.launch("image/*")
                }) {
                    Text("Sí, cambiar", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}