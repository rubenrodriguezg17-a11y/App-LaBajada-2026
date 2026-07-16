package com.labajada.app.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.R
import com.labajada.app.presentation.shared.theme.*

@Composable
fun OnboardingScreen(
    onBuyerSelected: () -> Unit,
    onRestaurantSelected: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo_gastronomico),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.75f),
                            Color.Black.copy(alpha = 0.95f)
                        ),
                        startY = 0f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 64.dp)
            ) {
                Text(
                    text = "La Bajada",
                    fontSize = 56.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1.5).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(OroLiquido, shape = RoundedCornerShape(50.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "LET'S GOO'",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1C1E21),
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "El mapa definitivo de los huariques y bajadas.",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "¡Te damos la bienvenida a la comunidad gastronómica más picante! Descubre sabores auténticos a la vuelta de la esquina o pon a rugir los motores de tu negocio.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    lineHeight = 22.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "¿CUÁL ES TU ROL HOY?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable { onBuyerSelected() },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Quiero Buscar Comida",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Encuentra tu bajada al toque cerca de ti.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable { onRestaurantSelected() },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.04f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Soy un Restaurante",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gestiona tu cocina y recibe nuevos clientes.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                }
            }
        }
    }
}