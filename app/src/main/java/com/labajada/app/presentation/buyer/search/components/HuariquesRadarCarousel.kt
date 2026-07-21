package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.labajada.app.presentation.shared.theme.*

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
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        modifier = Modifier.fillMaxWidth()
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
                    .width(185.dp)
                    .alpha(if (huarique.isOpen) 1f else 0.6f)
                    .clickable {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(huarique.latitud, huarique.longitud), 18.0f
                        )
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BordeSuave.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SuperficieCampo),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableRestaurant,
                                    contentDescription = null,
                                    tint = TextoSecundario,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Scrim reforzado: 3 paradas para asegurar legibilidad del nombre
                        // aunque la foto de portada sea muy clara/luminosa (cielos, paredes blancas, etc.)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.65f)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colorStops = arrayOf(
                                            0f to Color.Transparent,
                                            0.5f to Color.Black.copy(alpha = 0.35f),
                                            1f to Color.Black.copy(alpha = 0.82f)
                                        )
                                    )
                                )
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                        ) {
                            RestaurantBadgeChip(nivelInsignia)
                        }

                        if (!huarique.isOpen) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 8.dp, top = 36.dp),
                                color = Color(0xFFFFEBEE),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "Cerrado",
                                    fontSize = 9.sp,
                                    fontFamily = Nunito,
                                    fontWeight = FontWeight.Bold,
                                    color = RojoAlerta,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
                                .padding(8.dp)
                                .background(Color.White, CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) RojoGochujang else TextoSecundario,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = huarique.nombre,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = Baloo2,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.55f),
                                    offset = Offset(0f, 1f),
                                    blurRadius = 6f
                                )
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "${huarique.distancia} • ${huarique.category}",
                            fontSize = 12.sp,
                            fontFamily = Nunito,
                            color = TextoSecundario,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { onVerMenu(huarique) },
                            enabled = huarique.isOpen,
                            contentPadding = PaddingValues(vertical = 0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RojoGochujang,
                                disabledContainerColor = SuperficieCampo,
                                disabledContentColor = TextoSecundario
                            )
                        ) {
                            Text(
                                text = if (huarique.isOpen) "Ver menú" else "No disponible",
                                fontSize = 13.sp,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}