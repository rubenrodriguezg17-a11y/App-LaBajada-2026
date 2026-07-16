package com.labajada.app.presentation.restaurant.dashboard

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.model.Dish
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * GESTIÓN DEL MENÚ (PLATILLOS / DISHES)
 */

fun RestaurantDashboardViewModel.onDishNameChange(value: String) = _uiState.update { it.copy(dishName = value) }
fun RestaurantDashboardViewModel.onDishPriceChange(value: String) = _uiState.update { it.copy(dishPrice = value) }
fun RestaurantDashboardViewModel.onDishImageChange(uri: Uri?) = _uiState.update { it.copy(selectedImageUri = uri) }

fun RestaurantDashboardViewModel.prepararNuevoPlatillo() {
    _uiState.update { it.copy(isEditing = false, dishName = "", dishPrice = "", selectedImageUri = null) }
}

fun RestaurantDashboardViewModel.prepararEdicionPlatillo(index: Int, dish: Dish) {
    _uiState.update {
        it.copy(
            itemIndexToAction = index,
            itemIdToEdit = dish.id,
            isEditing = true,
            dishName = dish.name,
            dishPrice = dish.price.replace("S/. ", ""),
            selectedImageUri = if (dish.imagePath.isNotEmpty()) dish.imagePath.toUri() else null
        )
    }
}

fun RestaurantDashboardViewModel.prepararEliminacionPlatillo(index: Int, dishId: String) {
    _uiState.update { it.copy(itemIndexToAction = index, itemIdToEdit = dishId) }
}

/**
 * Un platillo es válido para guardar si tiene nombre (sin espacios en blanco) y un
 * precio numérico mayor a 0. El campo de precio en la UI ya restringe el formato con
 * regex, pero puede llegar vacío o en "0" si el usuario no lo llena.
 */
fun RestaurantDashboardViewModel.isDishFormValid(state: RestaurantDashboardState = uiState.value): Boolean {
    val precio = state.dishPrice.toDoubleOrNull()
    return state.dishName.isNotBlank() && precio != null && precio > 0.0
}

/**
 * Guarda el platillo actual. Devuelve false y no hace nada si el formulario no es
 * válido (nombre vacío o precio inválido/cero), para que quien la llame sepa que debe
 * mantener la hoja de formulario abierta en vez de cerrarla como si hubiera guardado.
 */
fun RestaurantDashboardViewModel.guardarPlatillo(context: Context): Boolean {
    val state = _uiState.value
    if (!isDishFormValid(state)) return false

    val formattedPrice = if (state.dishPrice.startsWith("S/. ")) state.dishPrice else "S/. ${state.dishPrice}"
    val finalImagePath = state.selectedImageUri?.let { uri ->
        if (uri.toString().startsWith("content://")) {
            guardarImagenEnAlmacenamientoInterno(context, uri) ?: ""
        } else {
            uri.toString()
        }
    } ?: ""

    val dishModel = Dish(
        id = if (state.isEditing) state.itemIdToEdit else UUID.randomUUID().toString(),
        restaurantId = currentRestaurantId,
        name = state.dishName,
        price = formattedPrice,
        imagePath = finalImagePath
    )

    viewModelScope.launch {
        if (state.isEditing) dishRepository.updateDishInMenu(dishModel)
        else dishRepository.saveDishToMenu(dishModel)
    }
    return true
}

fun RestaurantDashboardViewModel.eliminarPlatillo(dishId: String) {
    viewModelScope.launch { dishRepository.deleteDishFromMenu(dishId) }
}
