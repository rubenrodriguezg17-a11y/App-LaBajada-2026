package com.labajada.app.presentation.restaurant.dashboard.components.perfil

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardState
import com.labajada.app.presentation.restaurant.dashboard.RestaurantDashboardViewModel
import com.labajada.app.presentation.restaurant.dashboard.enviarDocumentos
import com.labajada.app.presentation.restaurant.dashboard.onProfileMenuPhotoSelected
import com.labajada.app.presentation.restaurant.dashboard.onProfilePermitPhotoSelected
import com.labajada.app.presentation.restaurant.dashboard.onProfileStorePhotoSelected
import com.labajada.app.presentation.restaurant.register.components.RestaurantImagePicker

/**
 * Tarjeta expandible de documentos de verificación (foto de local, carta/menú, permiso
 * municipal) y el flujo de "enviar a revisión". Extraído de PerfilInfoSection para que
 * ese archivo no siga creciendo.
 */
@Composable
fun PerfilDocumentsCard(
    viewModel: RestaurantDashboardViewModel,
    state: RestaurantDashboardState
) {
    val context = LocalContext.current
    val documentsLocked = state.resDocumentsSubmittedAt != null && !state.resIsVerified
    val inlineIconId = "inlineIcon"
    val annotatedString = buildAnnotatedString {
        appendInlineContent(inlineIconId, "[icon]")
        append(" Sube tu permiso municipal y obtén la insignia de Verificado.")
    }
    val inlineContent = mapOf(
        inlineIconId to InlineTextContent(
            Placeholder(
                width = 18.sp,
                height = 18.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Insignia de Verificado",
                tint = Color(0xFF2196F3)
            )
        }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        var documentsExpanded by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { documentsExpanded = !documentsExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Documentos", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF263238))
                    Text(
                        text = when {
                            state.resDocumentsSubmittedAt != null && !state.resIsVerified -> "Enviados. En revisión"
                            state.resIsVerified -> "Verificado"
                            else -> "Toca para completar tus documentos"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
                Icon(
                    imageVector = if (documentsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E)
                )
            }

            AnimatedVisibility(visible = documentsExpanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RestaurantImagePicker(
                        label = "Foto de tu local o puesto",
                        imageUrl = state.resStorePhotoUrl,
                        enabled = !documentsLocked,
                        confirmChangeMessage = "Esta foto ya fue enviada a revisión. Si la cambias, tu insignia desaparecerá temporalmente hasta que se vuelva a verificar.",
                        onImageSelected = { uri -> viewModel.onProfileStorePhotoSelected(context, uri?.let { Uri.parse(it) }) }
                    )
                    RestaurantImagePicker(
                        label = "Foto de tu carta o menú (opcional)",
                        imageUrl = state.resMenuPhotoUrl,
                        enabled = !documentsLocked,
                        confirmChangeMessage = "Esta foto ya fue enviada a revisión. Si la cambias, tu insignia desaparecerá temporalmente hasta que se vuelva a verificar.",
                        onImageSelected = { uri -> viewModel.onProfileMenuPhotoSelected(context, uri?.let { Uri.parse(it) }) }
                    )

                    if (!state.resIsVerified) {
                        Text(
                            text = annotatedString,
                            inlineContent = inlineContent,
                            fontSize = 12.sp,
                            color = Color(0xFF616161)
                        )
                    }
                    RestaurantImagePicker(
                        label = "Foto de tu permiso municipal (opcional)",
                        imageUrl = state.resPermitPhotoUrl,
                        enabled = !documentsLocked,
                        confirmChangeMessage = "Este documento ya está en revisión o validado. Si lo cambias, tu insignia desaparecerá temporalmente hasta que se vuelva a verificar. Solo cámbialo si tu permiso venció o ya no es válido.",
                        onImageSelected = { uri -> viewModel.onProfilePermitPhotoSelected(context, uri?.let { Uri.parse(it) }) }
                    )

                    var showSubmitConfirm by remember { mutableStateOf(false) }

                    if (documentsLocked) {
                        Text(
                            text = "Tus documentos están en revisión. Te avisaremos por correo, o revisa aquí en un máximo de 24 horas.",
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    } else {
                        Button(
                            onClick = { showSubmitConfirm = true },
                            enabled = state.resStorePhotoUrl != null,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263238)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Enviar documentos", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    if (showSubmitConfirm) {
                        AlertDialog(
                            onDismissRequest = { showSubmitConfirm = false },
                            title = { Text("Documentos enviados", fontWeight = FontWeight.Bold) },
                            text = {
                                Text("Tus documentos pasarán por revisión. Espera un tiempo máximo de 24 horas — te avisaremos por correo, o puedes entrar a la app y ver tu insignia pegada junto al nombre de tu local.")
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    showSubmitConfirm = false
                                    viewModel.enviarDocumentos()
                                }) {
                                    Text("Entendido", fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
