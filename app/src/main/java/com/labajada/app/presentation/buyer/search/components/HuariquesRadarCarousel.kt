package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.labajada.app.core.ui.helpers.RestaurantBadgeChip
import com.labajada.app.core.ui.helpers.calcularNivelInsignia
import com.labajada.app.presentation.buyer.search.RadarHuarique
import com.labajada.app.presentation.buyer.search.BuyerSearchViewModel
import com.labajada.app.presentation.shared.others.ImageDimens

@Composable
fun HuariquesRadarCarousel(
    huariquesRadar: List<RadarHuarique>,
    cameraPositionState: CameraPositionState,
    searchViewModel: BuyerSearchViewModel,
    onVerMenu: (RadarHuarique) -> Unit
) {
    val favoritos by searchViewModel.restaurantesFavoritosRoom.collectAsState()
    val favoritosIds = remember(favoritos) { favoritos.map { it.restaurantId }.toSet() }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        items(huariquesRadar, key = { it.id }) { huarique ->
            val isFavorite = favoritosIds.contains(huarique.id)
            val nivelInsignia = calcularNivelInsignia(
                documentType = huarique.documentType,
                isVerified = huarique.isVerified,
                documentsSubmittedAt = huarique.documentsSubmittedAt,
                storePhotoUrl = huarique.storePhotoUrl,
                menuPhotoUrl = huarique.menuPhotoUrl,
                permitPhotoUrl = huarique.permitPhotoUrl
            )

            Card(
                modifier = Modifier
                    .width(220.dp)
                    .alpha(if (huarique.isOpen) 1f else 0.55f)
                    .clickable {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(huarique.latitud, huarique.longitud), 18.0f
                        )
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

//                    Banner de imagen: mismo ratio que la portada del restaurante
//                    (ImageDimens.RESTAURANT_COVER_RATIO), para que se vea completa
//                    y consistente con lo que el dueño subió como foto de portada.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ImageDimens.RESTAURANT_COVER_RATIO)
                    ) {
                        if (huarique.imageUrl != null) {
                            AsyncImage(
                                model = huarique.imageUrl,
                                contentDescription = huarique.nombre,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                    .background(Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableRestaurant,
                                    contentDescription = null,
                                    tint = Color(0xFFBDBDBD),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        if (!huarique.isOpen) {
                            Card(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Cerrado",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (isFavorite) {
                                    searchViewModel.quitarRestauranteDeFavoritos(huarique.id)
                                } else {
                                    searchViewModel.agregarRestauranteAFavoritos(
                                        id = huarique.id,
                                        nombre = huarique.nombre,
                                        categoria = huarique.category,
                                        direccion = huarique.distancia
                                    )
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(50))
                                .size(30.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) Color(0xFFD32F2F) else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Info debajo del banner
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = huarique.nombre,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF212121),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        RestaurantBadgeChip(nivelInsignia)

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${huarique.distancia} • ${huarique.category}",
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (huarique.isOpen) "Ver menú" else "No disponible por el momento",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (huarique.isOpen) Color(0xFFD32F2F) else Color.LightGray,
                            textDecoration = if (huarique.isOpen) TextDecoration.Underline else TextDecoration.None,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable(enabled = huarique.isOpen) { onVerMenu(huarique) }
                        )
                    }
                }
            }
        }
    }
}