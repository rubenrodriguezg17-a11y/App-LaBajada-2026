package com.labajada.app.presentation.shared.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.labajada.app.presentation.shared.theme.Bangers
import com.labajada.app.presentation.shared.theme.RojoGochujang

@Composable
fun TermsAndConditionsDialog(
    isSeller: Boolean,
    onDismiss: () -> Unit,
    onAccept: (() -> Unit)? = null
) {
    val rawContent = if (isSeller) TermsContent.sellerTerms else TermsContent.buyerTerms
    val scrollState = rememberScrollState()

    // Procesamos las líneas del texto original para darle un parseo visual premium
    val lines = rawContent.lines().map { it.trim() }.filter { it.isNotEmpty() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp) // Consistencia con el diseño de 12.dp de tus inputs
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Título principal con la tipografía de la app
                Text(
                    text = if (isSeller) "TÉRMINOS PARA NEGOCIOS" else "TÉRMINOS PARA COMPRADORES",
                    fontSize = 22.sp,
                    fontFamily = Bangers,
                    color = RojoGochujang
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Contenedor scrolleable con diseño estructurado
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    lines.forEach { line ->
                        when {
                            // Encabezado principal del documento
                            line.startsWith("Términos y Condiciones") -> {
                                Text(
                                    text = line,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF263238),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            // Títulos numéricos de las cláusulas (ej: "1. Objeto...")
                            line.firstOrNull()?.isDigit() == true -> {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = line,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF263238)
                                )
                            }
                            // Sub-puntos o incisos específicos (ej: "a) No prepara...")
                            line.startsWith("a)") || line.startsWith("b)") || line.startsWith("c)") || line.startsWith("d)") -> {
                                Row(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(
                                        text = line,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF424242),
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                            // Texto descriptivo normal
                            else -> {
                                Text(
                                    text = line,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF616161),
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (onAccept != null) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Aceptar y continuar",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cerrar",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238),
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}