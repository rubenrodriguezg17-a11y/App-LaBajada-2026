package com.labajada.app.presentation.restaurant.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PeruValidators
import com.labajada.app.presentation.shared.theme.*

private val rubrosGastronomicos = listOf(
    "Restaurante - Menú clásico", "Polleria", "Carretilla", "Comida Rapida", "Chifa"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterBusinessInfoStep(
    restaurantName: String,
    documentType: String,
    documentNumber: String,
    phoneNumber: String,
    selectedCategory: String,
    expandedCategory: Boolean,
    businessHours: String?,
    onNameChange: (String) -> Unit,
    onDocumentTypeChange: (String) -> Unit,
    onDocumentNumberChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onToggleCategoryDropdown: () -> Unit,
    onBusinessHoursChange: (String) -> Unit
) {
    val maxLength = if (documentType == "RUC") 11 else 8
    val isDocumentValid = documentNumber.isEmpty() ||
            if (documentType == "RUC") PeruValidators.isValidRuc(documentNumber) else PeruValidators.isValidDni(documentNumber)
    val isPhoneValid = phoneNumber.isEmpty() || PeruValidators.isValidPhone(phoneNumber)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "¡Dale, empecemos!",
            fontSize = 27.sp,
            fontFamily = Bangers,
            color = MarronSazon
        )
        Text(
            text = "Vamos a registrar tu negocio en La Bajada",
            fontSize = 14.sp,
            fontFamily = Nunito,
            color = TextoSecundarioRestaurante
        )

        Spacer(modifier = Modifier.height(4.dp))
        RegisterStepIndicator(currentStep = 1, totalSteps = 4)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "¿CÓMO SE LLAMA TU LOCAL?",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            OutlinedTextField(
                value = restaurantName,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (restaurantName.isNotBlank()) DoradoTostado else BordeCalidoRestaurante,
                    unfocusedBorderColor = if (restaurantName.isNotBlank()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "¿TU LOCAL TIENE RUC, O QUIERES USAR TU DNI?",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                listOf("DNI", "RUC").forEach { tipo ->
                    val isSelected = documentType == tipo
                    Column(
                        modifier = Modifier
                            .clickable {
                                onDocumentTypeChange(tipo)
                                onDocumentNumberChange("")
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tipo,
                            fontSize = 14.sp,
                            fontFamily = Nunito,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) DoradoTostado else TextoSecundarioRestaurante
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(2.dp)
                                .background(if (isSelected) DoradoTostado else Color.Transparent)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = documentNumber,
                onValueChange = { if (it.length <= maxLength && it.all(Char::isDigit)) onDocumentNumberChange(it) },
                placeholder = { Text(if (documentType == "RUC") "Ingresa tu RUC" else "Numero de documento", fontFamily = Nunito) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = !isDocumentValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (!isDocumentValid) RojoAlerta else if (documentNumber.isNotEmpty()) DoradoTostado else BordeCalidoRestaurante,
                    unfocusedBorderColor = if (!isDocumentValid) RojoAlerta else if (documentNumber.isNotEmpty()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                )
            )
            if (!isDocumentValid) {
                Text(
                    text = "Debe tener $maxLength dígitos.",
                    color = RojoAlerta,
                    fontSize = 12.sp,
                    fontFamily = Nunito,
                    modifier = Modifier.padding(start = 4.dp)
                )
            } else {
                Text(
                    text = "Este número de documento será validado inmediatamente para comprobar su veracidad.",
                    fontSize = 11.sp,
                    fontFamily = Nunito,
                    color = VerdeMatcha,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "TELÉFONO DE CONTACTO",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(SuperficieCampo, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text("+51", fontFamily = Nunito, fontWeight = FontWeight.Bold, color = TextoSecundarioRestaurante)
                }
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 9 && it.all(Char::isDigit)) onPhoneChange(it) },
                    placeholder = { Text("Ej. 987 654 321", fontFamily = Nunito) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = !isPhoneValid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (!isPhoneValid) RojoAlerta else if (phoneNumber.isNotEmpty()) DoradoTostado else BordeCalidoRestaurante,
                        unfocusedBorderColor = if (!isPhoneValid) RojoAlerta else if (phoneNumber.isNotEmpty()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                    )
                )
            }
            if (!isPhoneValid) {
                Text(
                    text = "Celular inválido (9 dígitos, empieza con 9)",
                    color = RojoAlerta,
                    fontSize = 12.sp,
                    fontFamily = Nunito,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "¿CÓMO SE CONSIDERA TU NEGOCIO?",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { onToggleCategoryDropdown() }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Seleccionar categoría", fontFamily = Nunito) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (selectedCategory.isNotBlank()) DoradoTostado else BordeCalidoRestaurante,
                        unfocusedBorderColor = if (selectedCategory.isNotBlank()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { onToggleCategoryDropdown() }
                ) {
                    rubrosGastronomicos.forEach { rubro ->
                        DropdownMenuItem(
                            text = { Text(rubro, fontFamily = Nunito) },
                            onClick = { onCategorySelected(rubro) }
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "CUÉNTANOS: ¿CUÁNDO ABRES NORMALMENTE? (OPCIONAL)",
                fontSize = 13.sp,
                fontFamily = Baloo2,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                color = MarronSazon
            )
            OutlinedTextField(
                value = businessHours ?: "",
                onValueChange = onBusinessHoursChange,
                placeholder = { Text("Ejemplo: Lunes a viernes, de 6 pm a 11 pm", fontFamily = Nunito) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (!businessHours.isNullOrBlank()) DoradoTostado else BordeCalidoRestaurante,
                    unfocusedBorderColor = if (!businessHours.isNullOrBlank()) DoradoTostado.copy(alpha = 0.5f) else BordeCalidoRestaurante
                )
            )
            Text(
                text = "Opcional, pero trata de que el horario sea claro y entendible por si alguien se hace el curioso y revisa tu perfil.",
                fontSize = 12.sp,
                fontFamily = Nunito,
                color = TextoSecundarioRestaurante
            )
        }
    }
}