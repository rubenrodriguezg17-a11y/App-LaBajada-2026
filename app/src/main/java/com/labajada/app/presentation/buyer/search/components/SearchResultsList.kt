package com.labajada.app.presentation.buyer.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.labajada.app.domain.model.Dish
import com.labajada.app.presentation.buyer.cart.CartFlightBus

@Composable
fun SearchResultsList(
    platosEncontrados: List<Dish>,
    onAddDish: (Dish) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = "Platillos encontrados al toque",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (platosEncontrados.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron platos. ¡Intenta con otro antojo!",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)
            ) {
                itemsIndexed(
                    items = platosEncontrados,
                    key = { index, plato -> "${plato.id}_$index" }
                ) { _, plato ->
                    var buttonPosition by remember { mutableStateOf(Offset.Zero) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = if (plato.imagePath.startsWith("http") || plato.imagePath.startsWith("content")) {
                                    plato.imagePath
                                } else {
                                    "https://placeholder.com"
                                },
                                contentDescription = "Foto de ${plato.name}",
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF5F5F5)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plato.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = plato.price,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFD32F2F)
                                )
                            }

                            FilledIconButton(
                                onClick = {
                                    android.util.Log.d("CartFlight", "launchFlight start = $buttonPosition")
                                    CartFlightBus.launchFlight(buttonPosition)
                                    onAddDish(plato)

                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .onGloballyPositioned { coords ->
                                        val pos = coords.positionInWindow()
                                        buttonPosition = Offset(
                                            x = pos.x + coords.size.width / 2f,
                                            y = pos.y + coords.size.height / 2f
                                        )
                                    },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color(0xFF263238),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddShoppingCart,
                                    contentDescription = "Agregar al carrito",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}