package com.labajada.app.presentation.restaurant.dashboard.components.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.domain.model.Dish
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.dashboard.*

@Composable
fun MenuScreen(
    viewModel: RestaurantDashboardViewModel,
    platillosDelDia: List<Dish>
) {
    if (platillosDelDia.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = "Menú vacío",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF263238)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tu menú está vacío",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238)
                )
                Text(
                    text = "Toca el botón + para agregar tu primer platillo",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(platillosDelDia, key = { _, dish -> dish.id }) { index, dish ->
                DishRowItem(
                    name = dish.name,
                    price = dish.price,
                    imageUrl = dish.imagePath,
                    onEdit = {
                        viewModel.prepararEdicionPlatillo(index, dish)
                        viewModel.toggleFormSheet(true)
                    },
                    onDelete = {
                        viewModel.prepararEliminacionPlatillo(index, dish.id)
                        viewModel.toggleDeleteDialog(true)
                    }
                )
            }
        }
    }
}