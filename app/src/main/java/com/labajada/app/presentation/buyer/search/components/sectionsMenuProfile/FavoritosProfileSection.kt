package com.labajada.app.presentation.buyer.search.components.sectionsMenuProfile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // ◄ Variante AutoMirrored moderna recomendada por Android
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel

@Composable
fun FavoritosProfileSection(
    viewModel: BuyerSearchViewModel,
    onSectionChange: (String) -> Unit
) {
    val favoritosReal by viewModel.restaurantesFavoritosRoom.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onSectionChange("MENU") }
                .padding(bottom = 16.dp)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Mis Huariques Favoritos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (favoritosReal.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no has guardado restaurantes favoritos.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(favoritosReal, key = { index, restaurante -> "${restaurante.restaurantId}_$index" }) { index, restaurante ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = restaurante.restaurantName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = restaurante.category,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF757575)
                                )
                                Text(
                                    text = restaurante.address,
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }

                            IconButton(onClick = { viewModel.quitarRestauranteDeFavoritos(restaurante.restaurantId) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
