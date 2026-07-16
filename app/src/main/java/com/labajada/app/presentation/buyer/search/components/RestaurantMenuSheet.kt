package com.labajada.app.presentation.buyer.search.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.labajada.app.domain.model.Dish
import com.labajada.app.presentation.buyer.search.RadarHuarique
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

private data class MenuFlightEvent(
    val id: String = UUID.randomUUID().toString(),
    val start: Offset
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMenuSheet(
    huarique: RadarHuarique,
    menu: List<Dish>,
    cartItemCount: Int = 0,
    onDismiss: () -> Unit,
    onDishAdded: (Dish) -> Unit
) {
    var cartBadgeAnchor by remember { mutableStateOf<Offset?>(null) }
    var contentOrigin by remember { mutableStateOf(Offset.Zero) }
    val flightEvents = remember { mutableStateListOf<MenuFlightEvent>() }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { contentOrigin = it.positionInRoot() }
        ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp).navigationBarsPadding()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(huarique.nombre, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))
                    Text("Toca + para agregar al carrito", fontSize = 13.sp, color = Color(0xFF757575))
                }

                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(containerColor = Color(0xFFD32F2F)) { Text("$cartItemCount") }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFECEFF1), shape = RoundedCornerShape(50.dp))
                            .size(40.dp)
                            .onGloballyPositioned { coords ->
                                val pos = coords.positionInRoot()
                                cartBadgeAnchor = Offset(
                                    x = pos.x + coords.size.width / 2f,
                                    y = pos.y + coords.size.height / 2f
                                )

                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color(0xFF212121))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (menu.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                    Text("Este huarique aún no publicó su menú del día.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp)
                ) {
                    items(menu, key = { it.id }) { plato ->
                        var showAddedFeedback by remember { mutableStateOf(false) }
                        val scale = remember { Animatable(1f) }
                        val scope = rememberCoroutineScope()

                        // Se mantiene el Box para superponer el badge sobre el Card
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = if (plato.imagePath.startsWith("http") || plato.imagePath.startsWith("content") || plato.imagePath.startsWith("/"))
                                            plato.imagePath else "https://placeholder.com",
                                        contentDescription = "Foto de ${plato.name}",
                                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF5F5F5)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(plato.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                                        Text(plato.price, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F))
                                    }

                                    var buttonPosition by remember { mutableStateOf(Offset.Zero) }

                                    FilledIconButton(
                                        onClick = {
                                            onDishAdded(plato)
                                            flightEvents.add(MenuFlightEvent(start = buttonPosition))
                                            showAddedFeedback = true
                                            scope.launch {
                                                scale.animateTo(1.25f, animationSpec = tween(120))
                                                scale.animateTo(1f, animationSpec = tween(120))
                                                delay(700)
                                                showAddedFeedback = false
                                            }
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .scale(scale.value)
                                            .onGloballyPositioned { coords ->
                                                val pos = coords.positionInRoot()
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
                                        Icon(Icons.Default.AddShoppingCart, contentDescription = "Agregar al carrito", modifier = Modifier.size(20.dp))
                                    }
                                }
                            }

                            androidx.compose.animation.AnimatedVisibility(
                                visible = showAddedFeedback,
                                exit = fadeOut(animationSpec = tween(400)) + slideOutVertically(targetOffsetY = { -it }),
                                modifier = Modifier.align(Alignment.TopEnd).padding(top = 4.dp, end = 8.dp)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = "+1 agregado",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // Puntos que "vuelan" desde el botón + de cada platillo hasta el ícono del carrito del sheet.
            flightEvents.toList().forEach { event ->
                key(event.id) {
                    MenuFlyingDot(
                        start = event.start - contentOrigin,
                        end = (cartBadgeAnchor ?: event.start) - contentOrigin,
                        onFinished = { flightEvents.remove(event) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuFlyingDot(start: Offset, end: Offset, onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val baseRadius = 10.dp

    LaunchedEffect(Unit) {
        launch {
            progress.animateTo(1f, animationSpec = tween(1200, easing = EaseInOutCubic))
            onFinished()
        }
        launch {
            delay(800)
            scale.animateTo(0f, animationSpec = tween(400, easing = EaseInOutCubic))
        }
    }

    val t = progress.value
    val x = start.x + (end.x - start.x) * t
    val arcHeight = -90f * (4 * t * (1 - t))
    val y = start.y + (end.y - start.y) * t + arcHeight

    Canvas(modifier = Modifier.fillMaxSize()) {
        val radiusPx = baseRadius.toPx()
        val currentRadius = radiusPx * scale.value

        translate(
            left = x - currentRadius,
            top = y - currentRadius
        ) {
            drawCircle(
                color = Color(0xFFD32F2F),
                radius = currentRadius,
                center = Offset.Zero
            )
        }
    }
}
