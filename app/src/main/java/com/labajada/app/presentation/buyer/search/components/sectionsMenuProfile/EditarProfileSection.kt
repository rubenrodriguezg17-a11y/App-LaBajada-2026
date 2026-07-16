package com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PeruValidators
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import kotlinx.coroutines.launch

@Composable
fun EditarProfileSection(
    viewModel: BuyerSearchViewModel,
    onSectionChange: (String) -> Unit
) {
    val nombreActual by viewModel.currentBuyerName.collectAsState()
    val telefonoActual by viewModel.currentBuyerPhone.collectAsState()
    val emailActual by viewModel.currentBuyerEmail.collectAsState()
    val isGoogleUser by viewModel.isGoogleUser.collectAsState()

    var editNombre by remember(nombreActual) { mutableStateOf(nombreActual) }
    var editTelefono by remember(telefonoActual) { mutableStateOf(telefonoActual) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val scrollForm = rememberScrollState()

    val telefonoValido = editTelefono.isBlank() || PeruValidators.isValidPhone(editTelefono)
    val puedeGuardar = editNombre.isNotBlank() && telefonoValido && !isLoading

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 450.dp)
            .verticalScroll(scrollForm)
    ) {
        Row(
            modifier = Modifier
                .clickable(enabled = !isLoading) { onSectionChange("MENU") }
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Información Personal",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = editNombre,
                onValueChange = { editNombre = it; error = null },
                label = { Text("Nombre") },
                singleLine = true,
                enabled = !isLoading,
                isError = editNombre.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = emailActual,
                onValueChange = {},
                label = { Text("Correo") },
                singleLine = true,
                enabled = false,
                supportingText = {
                    Text(
                        if (isGoogleUser) "Vinculado a tu cuenta de Google"
                        else "Para cambiarlo, usa \"Cambiar correo\" en Configuración"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = editTelefono,
                onValueChange = { editTelefono = it.filter { c -> c.isDigit() }; error = null },
                label = { Text("Número de Teléfono") },
                singleLine = true,
                enabled = !isLoading,
                isError = !telefonoValido,
                supportingText = {
                    if (!telefonoValido) {
                        Text("Debe ser un celular peruano válido (9 dígitos, empieza con 9)")
                    } else if (isGoogleUser && telefonoActual.isBlank()) {
                        Text("Google no comparte tu teléfono, agrégalo aquí")
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            if (error != null) {
                Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        error = null
                        val resultado = viewModel.actualizarPerfil(editNombre, editTelefono)
                        isLoading = false
                        resultado.onSuccess {
                            onSectionChange("MENU")
                        }.onFailure {
                            error = "No se pudo guardar. Intenta de nuevo."
                        }
                    }
                },
                enabled = puedeGuardar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121)),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
