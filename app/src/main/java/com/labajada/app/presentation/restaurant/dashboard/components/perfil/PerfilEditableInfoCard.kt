package com.labajada.app.presentation.restaurant.dashboard.components.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.core.validation.PeruValidators
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardState
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.dashboard.guardarDatosDelLocal
import com.labajada.app.presentation.restaurant.dashboard.onProfileAddressChange
import com.labajada.app.presentation.restaurant.dashboard.onProfileBusinessHoursChange
import com.labajada.app.presentation.restaurant.dashboard.onProfileCategorySelected
import com.labajada.app.presentation.restaurant.dashboard.onProfileMaxDeliveryDistanceChange
import com.labajada.app.presentation.restaurant.dashboard.onProfileNameChange
import com.labajada.app.presentation.restaurant.dashboard.onProfileOffersDeliveryChange
import com.labajada.app.presentation.restaurant.dashboard.onProfilePhoneChange
import com.labajada.app.presentation.restaurant.dashboard.onProfileRucChange
import com.labajada.app.presentation.restaurant.dashboard.toggleProfileCategoryDropdown
import com.labajada.app.presentation.restaurant.dashboard.toggleProfileMap
import java.util.Locale

/**
 * Tarjeta con los campos editables del local: nombre, documento, contacto, rubro,
 * dirección, horario, ubicación GPS y configuración de delivery.
 * Extraído de PerfilInfoSection para que ese archivo no siga creciendo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilEditableInfoCard(
    viewModel: RestaurantDashboardViewModel,
    state: RestaurantDashboardState
) {
    val maxDocLength = if (state.resDocumentType == "RUC") 11 else 8
    val isDocumentValid = state.resRucByOwner.isEmpty() ||
            if (state.resDocumentType == "RUC") PeruValidators.isValidRuc(state.resRucByOwner)
            else PeruValidators.isValidDni(state.resRucByOwner)
    val isPhoneValid = state.resPhoneByOwner.isEmpty() ||
            PeruValidators.isValidPhone(state.resPhoneByOwner)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            EditableTextRow(
                label = "Nombre de local",
                value = state.resNameByOwner,
                onValueChange = { viewModel.onProfileNameChange(it) },
                onConfirm = { viewModel.guardarDatosDelLocal() }
            )
            HorizontalDivider(color = Color(0xFFF5F5F5))
            EditableTextRow(
                label = state.resDocumentType,
                value = state.resRucByOwner,
                onValueChange = {
                    if (it.length <= maxDocLength && it.all(Char::isDigit)) viewModel.onProfileRucChange(it)
                },
                onConfirm = { viewModel.guardarDatosDelLocal() },
                isError = !isDocumentValid,
                errorText = if (!isDocumentValid) "Debe tener $maxDocLength dígitos." else null
            )
            HorizontalDivider(color = Color(0xFFF5F5F5))
            EditableTextRow(
                label = "Contacto",
                value = state.resPhoneByOwner,
                onValueChange = {
                    if (it.length <= 9 && it.all(Char::isDigit)) viewModel.onProfilePhoneChange(it)
                },
                onConfirm = { viewModel.guardarDatosDelLocal() },
                isError = !isPhoneValid,
                errorText = if (!isPhoneValid) "Debe tener 9 dígitos y empezar con 9." else null
            )

            HorizontalDivider(color = Color(0xFFF5F5F5))
            EditableCategoryRow(
                value = state.resCategoryByOwner,
                expanded = state.expandedProfileCategory,
                options = viewModel.categoriesDisponibles,
                onToggle = { viewModel.toggleProfileCategoryDropdown() },
                onSelect = {
                    viewModel.onProfileCategorySelected(it)
                    viewModel.guardarDatosDelLocal()
                }
            )
            HorizontalDivider(color = Color(0xFFF5F5F5))
            EditableTextRow(
                label = "Dirección",
                value = state.resAddressByOwner,
                onValueChange = { viewModel.onProfileAddressChange(it) },
                onConfirm = { viewModel.guardarDatosDelLocal() }
            )
            HorizontalDivider(color = Color(0xFFF5F5F5))
            EditableTextRow(
                label = "Horario habitual",
                value = state.resBusinessHours,
                placeholder = "Ej: Lun-Sáb 6pm a 11pm",
                onValueChange = { viewModel.onProfileBusinessHoursChange(it) },
                onConfirm = { viewModel.guardarDatosDelLocal() }
            )
            HorizontalDivider(color = Color(0xFFF5F5F5))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ubicación GPS", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                    Text(
                        text = "Lat: ${String.format(Locale.US, "%.4f", state.resLatitude)} | Lon: ${String.format(Locale.US, "%.4f", state.resLongitude)}",
                        fontSize = 13.sp,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(onClick = { viewModel.toggleProfileMap(true) }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Editar ubicación", tint = Color(0xFFD32F2F))
                }
            }
            HorizontalDivider(color = Color(0xFFF5F5F5))

            // --- Delivery: activar/desactivar + distancia máxima ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Haces delivery?", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF212121))
                    Switch(
                        checked = state.resOffersDelivery,
                        onCheckedChange = {
                            viewModel.onProfileOffersDeliveryChange(it)
                            viewModel.guardarDatosDelLocal()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFD32F2F))
                    )
                }
                if (state.resOffersDelivery) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hasta ${String.format(Locale.US, "%.1f", state.resMaxDeliveryDistanceKm)} km",
                        fontSize = 13.sp,
                        color = Color(0xFF757575)
                    )
                    Slider(
                        value = state.resMaxDeliveryDistanceKm.toFloat(),
                        onValueChange = { viewModel.onProfileMaxDeliveryDistanceChange(it.toDouble()) },
                        onValueChangeFinished = { viewModel.guardarDatosDelLocal() },
                        valueRange = 0.5f..10f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFFD32F2F), activeTrackColor = Color(0xFFD32F2F))
                    )
                }
            }
        }
    }
}

@Composable
private fun EditableTextRow(
    label: String,
    value: String,
    placeholder: String = "",
    isError: Boolean = false,
    errorText: String? = null,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = Color(0xFF9E9E9E))
            if (isEditing) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = if (placeholder.isNotBlank()) {
                        { Text(placeholder, fontSize = 13.sp) }
                    } else null,
                    singleLine = true,
                    isError = isError,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                )
                if (isError && errorText != null) {
                    Text(
                        text = errorText,
                        color = Color(0xFFD32F2F),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            } else {
                Text(
                    text = value.ifBlank { "No registrado" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (isEditing) {
                    if (!isError) {
                        onConfirm()
                        isEditing = false
                    }
                } else {
                    isEditing = true
                }
            }
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                contentDescription = if (isEditing) "Guardar" else "Editar",
                tint = if (isEditing) (if (isError) Color(0xFFBDBDBD) else Color(0xFF2E7D32)) else Color(0xFF757575),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableCategoryRow(
    value: String,
    expanded: Boolean,
    options: List<String>,
    onToggle: () -> Unit,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Rubro", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                Text(
                    text = value.ifBlank { "No registrado" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
            }
            IconButton(onClick = onToggle) {
                Icon(Icons.Default.Edit, contentDescription = "Editar rubro", tint = Color(0xFF757575), modifier = Modifier.size(20.dp))
            }
        }

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { onToggle() }) {
            Spacer(modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true))
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = onToggle) {
                options.forEach { item ->
                    DropdownMenuItem(text = { Text(item) }, onClick = { onSelect(item) })
                }
            }
        }
    }
}
