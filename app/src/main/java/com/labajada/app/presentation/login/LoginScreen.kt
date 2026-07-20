package com.labajada.app.presentation.login

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.labajada.app.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labajada.app.presentation.shared.theme.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (String) -> Unit,
    onNeedsEmailVerification: (String) -> Unit,
    onGoogleNewUser: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }

    var isGoogleLoading by remember { mutableStateOf(false) }
    val googleButtonBusy = isGoogleLoading || uiState.isLoading

    val blurRadius by animateDpAsState(
        targetValue = if (googleButtonBusy) 14.dp else 0.dp,
        animationSpec = tween(350),
        label = "login_blur"
    )

    var rootOrigin by remember { mutableStateOf(Offset.Zero) }
    var googleButtonOffsetPx by remember { mutableStateOf(Offset.Zero) }
    var googleButtonSizePx by remember { mutableStateOf(IntSize.Zero) }

    val isEmailValid = remember(uiState.email) {
        android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email.trim()).matches()
    }
    val isPasswordValid = remember(uiState.password) {
        uiState.password.isNotBlank()
    }

    val sheetState = rememberModalBottomSheetState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IvoryBackground)
            .onGloballyPositioned { rootOrigin = it.positionInRoot() }
    ) {
        // ---- Todo el formulario, en UN SOLO contenedor con blur (sin grietas) ----
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .blur(blurRadius)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bienvenida: verde domina la pantalla de login (calma/confianza), el rojo
                // queda reservado solo para el wordmark y acentos puntuales.
                Text(
                    text = "La Bajada",
                    fontSize = 46.sp,
                    fontFamily = Bangers,
                    color = RojoGochujang,
                    letterSpacing = 0.3.sp
                )
                Text(
                    text = "¡Qué bueno verte de nuevo!",
                    fontSize = 16.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    color = TextoSecundario,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Ingresa tus credenciales",
                    fontSize = 18.sp,
                    fontFamily = Baloo2,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo Electrónico", fontFamily = Nunito, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = { Icon(Icons.Outlined.Email, null, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !uiState.isLoading && !isGoogleLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SuperficieCampo,
                        unfocusedContainerColor = SuperficieCampo,
                        focusedBorderColor = if (isEmailValid) VerdeMatcha else RojoAlerta,
                        unfocusedBorderColor = if (isEmailValid) VerdeMatcha.copy(alpha = 0.5f) else BordeSuave,
                        cursorColor = VerdeMatcha,
                        focusedLabelColor = TextoPrincipal
                    )
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña", fontFamily = Nunito, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !uiState.isLoading && !isGoogleLoading,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Nunito),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SuperficieCampo,
                        unfocusedContainerColor = SuperficieCampo,
                        focusedBorderColor = if (isPasswordValid) VerdeMatcha else RojoAlerta,
                        unfocusedBorderColor = if (isPasswordValid) VerdeMatcha.copy(alpha = 0.5f) else BordeSuave,
                        cursorColor = VerdeMatcha,
                        focusedLabelColor = TextoPrincipal
                    )
                )

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontSize = 13.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeMatcha,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !uiState.isLoading && !isGoogleLoading) { onNavigateToForgotPassword() }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón primario en Verde Matcha sólido: login es el momento de "confía en
                // nosotros", no el de "antojo" — el rojo de apetito se reserva para explorar/comprar.
                Button(
                    onClick = { viewModel.login(onLoginSuccess, onNeedsEmailVerification) },
                    enabled = isEmailValid && isPasswordValid && !uiState.isLoading && !isGoogleLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeMatcha,
                        disabledContainerColor = VerdeMatcha.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            "Iniciar Sesión",
                            fontSize = 16.sp,
                            fontFamily = Baloo2,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BordeSuave)
                    Text(
                        text = "  o  ",
                        fontSize = 13.sp,
                        fontFamily = Nunito,
                        color = TextoSecundario
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BordeSuave)
                }

                // Hueco invisible que reserva el espacio del botón de Google.
                // El botón REAL se dibuja aparte, más abajo, sin blur.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .onGloballyPositioned { coords ->
                            googleButtonOffsetPx = coords.positionInRoot() - rootOrigin
                            googleButtonSizePx = coords.size
                        }
                )

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿No tienes cuenta? ", fontSize = 14.sp, fontFamily = Nunito, color = TextoSecundario)
                    Text(
                        "Regístrate aquí",
                        fontSize = 14.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        color = RojoGochujang,
                        modifier = Modifier.clickable { onNavigateToOnboarding() }
                    )
                }
            }
        }

        // Botón de Google: negro sólido, tal como ya estaba — es exactamente el uso de
        // negro "de alto peso" que define el sistema (acción secundaria importante, no marca).
        if (googleButtonSizePx != IntSize.Zero) {
            Button(
                onClick = {
                    isGoogleLoading = true
                    coroutineScope.launch {
                        val result = GoogleSignInHelper.obtenerIdToken(context)
                        isGoogleLoading = false
                        result
                            .onSuccess { idToken ->
                                viewModel.loginWithGoogle(
                                    idToken = idToken,
                                    onExistingUser = onLoginSuccess,
                                    onNewUser = onGoogleNewUser,
                                    onNeedsEmailVerification = onNeedsEmailVerification
                                )
                            }
                            .onFailure { error ->
                                viewModel.onGoogleLoginError(error.localizedMessage ?: "No se pudo iniciar sesión con Google.")
                            }
                    }
                },
                enabled = !googleButtonBusy,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = googleButtonOffsetPx.x.roundToInt(),
                            y = googleButtonOffsetPx.y.roundToInt()
                        )
                    }
                    .size(
                        width = with(density) { googleButtonSizePx.width.toDp() },
                        height = with(density) { googleButtonSizePx.height.toDp() }
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NegroContorno,
                    disabledContainerColor = NegroContorno.copy(alpha = 0.55f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (googleButtonBusy) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Conectando con Google...", fontSize = 15.sp, fontFamily = Baloo2, fontWeight = FontWeight.Bold, color = Color.White)
                } else {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color.White, shape = RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Continuar con Google", fontSize = 15.sp, fontFamily = Baloo2, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = RojoAlerta)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = RojoAlerta,
                        fontSize = 14.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (uiState.showRoleSelector) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissRoleSelector() },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp, top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(BordeSuave, RoundedCornerShape(2.dp))
                )

                Text(
                    "¿Cómo quieres entrar hoy?",
                    fontSize = 21.sp,
                    fontFamily = Baloo2,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Comensal → Naranja Cercanía (mismo color que el resto del flujo comprador)
                Surface(
                    onClick = { viewModel.selectRole("BUYER") },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NaranjaCercania.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Restaurant, null, tint = NaranjaCercania, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Entrar como Comensal", fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Buscar la mejor comida cerca de ti", fontFamily = Nunito, color = TextoSecundario, fontSize = 13.sp)
                        }
                    }
                }

                // Restaurante → Marrón Sazón (identidad del lado negocio, no gris genérico)
                Surface(
                    onClick = { viewModel.selectRole("RESTAURANT") },
                    shape = RoundedCornerShape(20.dp),
                    color = MarronSazon,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SoupKitchen, null, tint = DoradoTostado, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Gestionar mi Restaurante", fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Text("Administrar pedidos y menú", fontFamily = Nunito, color = Color.White.copy(0.75f), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}