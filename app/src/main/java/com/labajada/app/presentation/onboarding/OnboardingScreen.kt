package com.labajada.app.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

        // Overlay cálido (marrón sazón → negro), en vez de negro plano:
        // mantiene la legibilidad del texto sobre la foto pero sin perder la calidez de marca.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MarronSazon.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.72f),
                            Color.Black.copy(alpha = 0.94f)
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
                    fontSize = 52.sp,
                    fontFamily = Bangers,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .background(DoradoTostado, shape = RoundedCornerShape(50.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "LET'S GOO'",
                        fontSize = 12.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        color = MarronSazon,
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "El mapa definitivo de los huariques y bajadas.",
                    fontSize = 18.sp,
                    fontFamily = Baloo2,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "¡Te damos la bienvenida a la comunidad gastronómica más picante! Descubre sabores auténticos a la vuelta de la esquina o pon a rugir los motores de tu negocio.",
                    fontSize = 14.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.8f),
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
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.65f),
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )

                // Comprador → acento Naranja Cercanía (accesible, rápido)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = 1.5.dp,
                            color = NaranjaCercania.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onBuyerSelected() },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Quiero Buscar Comida",
                            fontSize = 19.sp,
                            fontFamily = Baloo2,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Encuentra tu bajada al toque cerca de ti.",
                            fontSize = 13.sp,
                            fontFamily = Nunito,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                // Restaurante → acento Dorado Tostado (acogedor, artesanal)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = 1.5.dp,
                            color = DoradoTostado.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onRestaurantSelected() },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Soy un Restaurante",
                            fontSize = 19.sp,
                            fontFamily = Baloo2,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gestiona tu cocina y recibe nuevos clientes.",
                            fontSize = 13.sp,
                            fontFamily = Nunito,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}