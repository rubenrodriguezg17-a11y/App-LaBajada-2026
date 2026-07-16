package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Documentos y fotos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF263238)
        )
        Text(
            text = "Esto ayuda a que los clientes confíen más en tu negocio.",
            fontSize = 13.sp,
            color = Color.Gray
        )

        RestaurantImagePicker(
            label = "Foto de tu local o puesto",
            subtitle = "Obligatoria. Así verán tu negocio antes de pedir.",
            imageUrl = storePhotoUrl,
            onImageSelected = onStorePhotoSelected
        )

        RestaurantImagePicker(
            label = "Foto de tu carta o menú (opcional)",
            imageUrl = menuPhotoUrl,
            onImageSelected = onMenuPhotoSelected
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF8E1), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("✅", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Obtén la insignia de Verificado",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238)
                )
            }
            Text(
                text = "Sube tu permiso municipal de uso de espacio público (o licencia de funcionamiento) y tu negocio aparecerá con un sello de confianza ante los clientes. Es opcional, pero recomendado.",
                fontSize = 12.sp,
                color = Color(0xFF616161)
            )
            RestaurantImagePicker(
                label = "Foto de tu permiso municipal (opcional)",
                imageUrl = permitPhotoUrl,
                onImageSelected = onPermitPhotoSelected
            )
        }
    }
}