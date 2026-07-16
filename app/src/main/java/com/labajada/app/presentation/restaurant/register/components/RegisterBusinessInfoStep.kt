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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PeruValidators

private val rubrosGastronomicos = listOf(
    "Menú clásico", "Cevichería", "Criollo", "Fast Food / Bajadas", "Pollería", "Chifa"
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
    onNameChange: (String) -> Unit,
    onDocumentTypeChange: (String) -> Unit,
    onDocumentNumberChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onToggleCategoryDropdown: () -> Unit
) {
    val maxLength = if (documentType == "RUC") 11 else 8
    val isDocumentValid = documentNumber.isEmpty() ||
            if (documentType == "RUC") PeruValidators.isValidRuc(documentNumber) else PeruValidators.isValidDni(documentNumber)
    val isPhoneValid = phoneNumber.isEmpty() || PeruValidators.isValidPhone(phoneNumber)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Paso 1: Identidad del Negocio",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF263238)
        )

        Text(
            text = "Nombre del Restaurante",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF616161)
        )
        OutlinedTextField(
            value = restaurantName,
            onValueChange = onNameChange,
            placeholder = { Text("Ej. La Bajada Express") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Text(
            text = "Documento",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF616161)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("DNI" to "DNI (8 dígitos)", "RUC" to "RUC (11 dígitos)").forEach { (tipo, etiqueta) ->
                val isSelected = documentType == tipo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFFD32F2F) else Color.Transparent)
                        .clickable {
                            onDocumentTypeChange(tipo)
                            onDocumentNumberChange("") // limpia el número al cambiar de tipo
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = etiqueta,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF616161)
                    )
                }
            }
        }

        Text(
            text = "Número de Documento",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF616161)
        )
        OutlinedTextField(
            value = documentNumber,
            onValueChange = { if (it.length <= maxLength && it.all(Char::isDigit)) onDocumentNumberChange(it) },
            placeholder = { Text(if (documentType == "RUC") "Ingresa tu RUC" else "Ingresa tu DNI") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = !isDocumentValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (!isDocumentValid) {
            Text(
                text = "Debe tener $maxLength dígitos.",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Text(
            text = "Teléfono de Contacto",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF616161)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text("+51", fontWeight = FontWeight.Bold, color = Color(0xFF616161))
            }
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { if (it.length <= 9 && it.all(Char::isDigit)) onPhoneChange(it) },
                placeholder = { Text("Ej. 987 654 321") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = !isPhoneValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }
        if (!isPhoneValid) {
            Text(
                text = "Celular inválido (9 dígitos, empieza con 9)",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Text(
            text = "Categoría Gastronómica",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF616161)
        )
        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { onToggleCategoryDropdown() }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Seleccionar categoría") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { onToggleCategoryDropdown() }
            ) {
                rubrosGastronomicos.forEach { rubro ->
                    DropdownMenuItem(
                        text = { Text(rubro) },
                        onClick = { onCategorySelected(rubro) }
                    )
                }
            }
        }
    }
}