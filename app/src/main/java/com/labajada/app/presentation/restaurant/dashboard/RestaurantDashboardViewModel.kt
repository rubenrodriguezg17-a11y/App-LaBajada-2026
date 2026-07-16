package com.labajada.app.presentation.restaurant.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labajada.app.domain.repository.DishRepository
import com.labajada.app.domain.repository.OrderRepository
import com.labajada.app.domain.repository.RestaurantRepository
import com.labajada.app.domain.repository.UserPreferencesRepository
import com.labajada.app.domain.repository.UserRepository
import com.labajada.app.domain.usecase.auth.GetActiveUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel del dashboard del restaurante.
 *
 * Este archivo contiene SOLO el estado compartido, los flujos reactivos y la
 * inicialización. La lógica de cada sección vive en archivos de extensión
 * separados dentro del mismo paquete, para que el archivo no vuelva a crecer
 * hasta volverse difícil de navegar:
 *  - RestaurantDashboardViewModel+Profile.kt  → edición de perfil del local, fotos, apertura/cierre
 *  - RestaurantDashboardViewModel+Menu.kt     → alta/edición/borrado de platillos
 *  - RestaurantDashboardViewModel+Orders.kt   → cambio de estado de pedidos
 *  - RestaurantDashboardViewModel+Account.kt  → cambio de rol y desactivación de cuenta
 *
 * Las propiedades marcadas `internal` (en vez de `private`) existen para que esos
 * archivos de extensión puedan usarlas; siguen sin ser parte de la API pública
 * fuera del módulo.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantDashboardViewModel(
    internal val restaurantRepository: RestaurantRepository,
    internal val dishRepository: DishRepository,
    internal val orderRepository: OrderRepository,
    val userRepository: UserRepository,
    internal val userPreferencesRepository: UserPreferencesRepository,
    private val getActiveUserUseCase: GetActiveUserUseCase
) : ViewModel() {

    //ESTADOS DE LA INTERFAZ DE USUARIO (UI STATES)
    internal val _uiState = MutableStateFlow(RestaurantDashboardState())
    val uiState: StateFlow<RestaurantDashboardState> = _uiState.asStateFlow()

    internal val _isDualRole = MutableStateFlow(false)
    val isDualRole: StateFlow<Boolean> = _isDualRole.asStateFlow()

    internal val _isGoogleUser = MutableStateFlow(false)
    val isGoogleUser : StateFlow<Boolean> = _isGoogleUser.asStateFlow()

    // VARIABLES DE CONTROL INTERNO (PROPIEDADES)
    internal var currentRestaurantId: String = ""
    internal var currentOwnerId: String = ""
    val categoriesDisponibles = listOf("Cevichería", "Criollo", "Fast Food / Bajadas", "Pollería", "Chifa")

    // FLUJOS DE DATOS REACTIVOS (FLOWS & KOTLIN COROUTINES)
    private val activeSessionFlow = flow {
        val session = userRepository.getActiveSession()
        currentOwnerId = session?.userId ?: ""
        userRepository.refreshEmailFromFirebase()
        // Se re-lee la sesión después del refresh y se emite ESA versión (no la inicial),
        // para que el email/datos reflejen lo que acaba de sincronizarse con Firebase.
        val sessionActualizada = userRepository.getActiveSession()
        emit(sessionActualizada)
    }

    private val restaurantOrNullFlow = activeSessionFlow.flatMapLatest { session ->
        if (session != null) {
            restaurantRepository.getRestaurantByOwnerId(session.userId)
        } else {
            flowOf(null)
        }
    }

    val activeSession = restaurantOrNullFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val hasRestaurant: StateFlow<Boolean?> = restaurantOrNullFlow
        .map { restaurant -> restaurant != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val fallbackRestaurantName = activeSessionFlow.flatMapLatest { session ->
        flowOf(session?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Huarique")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Huarique")

    val platillosDelDia = activeSession.flatMapLatest { restaurant ->
        if (restaurant != null) dishRepository.getMenuOfTheDay(restaurant.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosActivos = activeSession.flatMapLatest { restaurant ->
        if (restaurant != null) orderRepository.getActiveOrders(restaurant.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosCompletados = activeSession.flatMapLatest { restaurant ->
        if (restaurant != null) orderRepository.getCompletedOrders(restaurant.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gananciasHoy = pedidosCompletados
        .map { completados ->
            val inicioDeHoy = java.time.LocalDate.now()
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            completados
                .filter { it.timestamp >= inicioDeHoy }
                .sumOf { it.totalPrice }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // CICLO DE VIDA / INICIALIZACIÓN
    init {
        viewModelScope.launch {
            activeSession.collect { restaurant ->
                restaurant?.let { res ->
                    currentRestaurantId = res.id
                    _uiState.update { it.copy(
                        resNameByOwner = res.restaurantName,
                        resDocumentType = res.documentType,
                        resRucByOwner = res.documentNumber,
                        resPhoneByOwner = res.phoneNumber,
                        resCategoryByOwner = res.selectedCategory,
                        resAddressByOwner = res.addressDetails,
                        resLatitude = res.latitude,
                        resLongitude = res.longitude,
                        resOffersDelivery = res.offersDelivery,
                        resMaxDeliveryDistanceKm = res.maxDeliveryDistanceKm,
                        resImageUrl = res.imageUrl,
                        resIsOpen = res.isOpen,
                        resBusinessHours = res.businessHours ?: "",
                        resStorePhotoUrl = res.storePhotoUrl,
                        resMenuPhotoUrl = res.menuPhotoUrl,
                        resPermitPhotoUrl = res.permitPhotoUrl,
                        resIsVerified = res.isVerified,
                        resDocumentsSubmittedAt = res.documentsSubmittedAt
                    )}
                    viewModelScope.launch {
                        val user = getActiveUserUseCase()
                        _isDualRole.value = user?.isBuyer == true && user.isOwner
                        _isGoogleUser.value = userRepository.isGoogleUser()
                    }
                }
            }
        }
    }

    // EVENTOS GENERALES DE LA UI & NAVEGACIÓN
    fun onTabSelected(index: Int) = _uiState.update { it.copy(selectedTab = index) }
    fun toggleDeleteDialog(show: Boolean) = _uiState.update { it.copy(showDeleteDialog = show) }
    fun toggleFormSheet(show: Boolean) = _uiState.update { it.copy(showFormSheet = show) }
    fun toggleGananciasVisibility() {
        _uiState.update { it.copy(isGananciasVisible = !it.isGananciasVisible) }
    }
    fun toggleConfigSection(show: Boolean) = _uiState.update { it.copy(showConfigSection = show) }
}
