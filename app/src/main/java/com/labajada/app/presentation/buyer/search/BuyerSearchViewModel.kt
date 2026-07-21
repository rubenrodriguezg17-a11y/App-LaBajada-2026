package com.labajada.app.presentation.buyer.search

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.labajada.app.core.extensions.toPrecioDouble
import com.labajada.app.domain.model.Dish
import com.labajada.app.domain.model.FavoriteRestaurant
import com.labajada.app.domain.repository.DishRepository
import com.labajada.app.domain.repository.RestaurantRepository
import com.labajada.app.domain.repository.UserPreferencesRepository
import com.labajada.app.domain.repository.UserRepository
import com.labajada.app.domain.usecase.auth.GetActiveUserUseCase
import com.labajada.app.domain.usecase.restaurant.GetActiveRestaurantsUseCase
import com.labajada.app.domain.usecase.search.ClearSearchHistoryUseCase
import com.labajada.app.domain.usecase.search.DeleteSearchQueryUseCase
import com.labajada.app.domain.usecase.search.GetAllDishesUseCase
import com.labajada.app.domain.usecase.search.GetRecentSearchHistoryUseCase
import com.labajada.app.domain.usecase.search.ManageFavoriteRestaurantUseCase
import com.labajada.app.domain.usecase.search.SaveSearchQueryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
class BuyerSearchViewModel(
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase,
    private val getRecentSearchHistoryUseCase: GetRecentSearchHistoryUseCase,
    private val manageFavoriteRestaurantUseCase: ManageFavoriteRestaurantUseCase,
    private val getActiveUserUseCase: GetActiveUserUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
    private val deleteSearchQueryUseCase: DeleteSearchQueryUseCase,
    private val getAllDishesUseCase: GetAllDishesUseCase,
    private val dishRepository: DishRepository,
    private val restaurantRepository: RestaurantRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    val userRepository: UserRepository,
    private val getActiveRestaurantsUseCase: GetActiveRestaurantsUseCase,
) : ViewModel() {

    private val _platosEncontrados = MutableStateFlow<List<Dish>>(emptyList())
    val platosEncontrados: StateFlow<List<Dish>> = _platosEncontrados.asStateFlow()

    private val _isDualRole = MutableStateFlow(false)
    val isDualRole: StateFlow<Boolean> = _isDualRole.asStateFlow()

    private val _isGoogleUser = MutableStateFlow(false)
    val isGoogleUser: StateFlow<Boolean> = _isGoogleUser.asStateFlow()
    private val _userLocation = MutableStateFlow(LatLng(-8.1116, -79.0287))
    val userLocation: StateFlow<LatLng> = _userLocation.asStateFlow()

    private val _currentBuyerName = MutableStateFlow("")
    val currentBuyerName: StateFlow<String> = _currentBuyerName.asStateFlow()

    private val _currentBuyerEmail = MutableStateFlow("")
    val currentBuyerEmail: StateFlow<String> = _currentBuyerEmail.asStateFlow()

    private val _currentBuyerPhone = MutableStateFlow("")
    val currentBuyerPhone: StateFlow<String> = _currentBuyerPhone.asStateFlow()

    private val _currentBuyerId = MutableStateFlow<String?>(null)
    val currentBuyerId: StateFlow<String?> = _currentBuyerId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _restaurantIdParaMenu = MutableStateFlow<String?>(null)

    val menuDelHuariqueSeleccionado: StateFlow<List<Dish>> = _restaurantIdParaMenu
        .flatMapLatest { id ->
            if (id != null) dishRepository.getMenuOfTheDay(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun abrirMenuDeHuarique(restaurantId: String) {
        _restaurantIdParaMenu.value = restaurantId
    }

    fun cerrarMenuDeHuarique() {
        _restaurantIdParaMenu.value = null
    }

    val searchHistory: StateFlow<List<String>> = _currentBuyerId
        .flatMapLatest { buyerId ->
            if (buyerId != null) getRecentSearchHistoryUseCase(buyerId)
                .map{
                    listaHistorial -> listaHistorial.distinct()
                }
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val restaurantesFavoritosRoom: StateFlow<List<FavoriteRestaurant>> = _currentBuyerId
        .flatMapLatest { buyerId ->
            if (buyerId != null) manageFavoriteRestaurantUseCase.getAll(buyerId)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val huariquesDesdeBaseDeDatos = restaurantRepository.getAllRestaurants()
        .combine(_userLocation) { listaRestaurants, ubicacionActual ->
            val todosLosPlatos = dishRepository.getAllMenuDishesOnce()
            listaRestaurants.map { restaurant ->
                val platosDelRestaurante = todosLosPlatos.filter { it.restaurantId == restaurant.id }
                val precioPromedioReal = if (platosDelRestaurante.isNotEmpty()) {
                    platosDelRestaurante.map { it.price.toPrecioDouble() }.average()
                } else {
                    0.0
                }
                RadarHuarique(
                    id = restaurant.id,
                    nombre = restaurant.restaurantName,
                    category = restaurant.selectedCategory,
                    precioPromedio = precioPromedioReal,
                    distancia = calcularDistanciaReal(
                        ubicacionActual.latitude,
                        ubicacionActual.longitude,
                        restaurant.latitude,
                        restaurant.longitude
                    ),
                    latitud = restaurant.latitude,
                    longitud = restaurant.longitude,
                    isOpen = restaurant.isOpen,
                    maxDeliveryDistanceKm = restaurant.maxDeliveryDistanceKm,
                    offersDelivery = restaurant.offersDelivery,
                    imageUrl = restaurant.imageUrl,
                    documentType = restaurant.documentType,
                    isVerified = restaurant.isVerified,
                    documentsSubmittedAt = restaurant.documentsSubmittedAt,
                    storePhotoUrl = restaurant.storePhotoUrl,
                    menuPhotoUrl = restaurant.menuPhotoUrl,
                    permitPhotoUrl = restaurant.permitPhotoUrl
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val session = userRepository.getActiveSession()
            _currentBuyerId.value = session?.userId

            userRepository.refreshEmailFromFirebase()

            val user = getActiveUserUseCase()
            if (user != null) {
                _currentBuyerName.value = user.fullName
                _currentBuyerEmail.value = user.email
                _currentBuyerPhone.value = user.phoneNumber
                _isDualRole.value = user.isBuyer && user.isOwner
            }
            _isGoogleUser.value = userRepository.isGoogleUser()
        }
    }

    /**
     * Actualiza nombre y teléfono del usuario actual (funciona igual para login con
     * email/contraseña o con Google, ya que ambos comparten la misma fila en Room).
     */
    suspend fun actualizarPerfil(nombre: String, telefono: String): Result<Unit> {
        val uid = requireBuyerId() ?: return Result.failure(Exception("No hay sesión activa."))
        val resultado = userRepository.updateProfile(uid, nombre, telefono)
        resultado.onSuccess { usuarioActualizado ->
            _currentBuyerName.value = usuarioActualizado.fullName
            _currentBuyerPhone.value = usuarioActualizado.phoneNumber
        }
        return resultado.map { }
    }

    /**
     * Devuelve el buyerId actual, esperando a que la sesión termine de cargar si aún no
     * lo hizo (evita que acciones como "agregar a favoritos" se pierdan en silencio si el
     * usuario las dispara justo al entrar a la pantalla).
     */
    private suspend fun requireBuyerId(): String? {
        _currentBuyerId.value?.let { return it }
        val session = userRepository.getActiveSession()
        return session?.userId?.also { _currentBuyerId.value = it }
    }

    @SuppressLint("MissingPermission")
    fun rastrearUbicacionActual(context: Context, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                // 1. Intento rápido: última ubicación cacheada.
                val ultimaConocida = fusedLocationClient.lastLocation.await()
                if (ultimaConocida != null) {
                    _userLocation.value = LatLng(ultimaConocida.latitude, ultimaConocida.longitude)
                    onResult(true)
                    return@launch
                }

                // 2. Fallback: pedir una ubicación fresca (emuladores o sin fix cacheado).
                val cts = CancellationTokenSource()
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()
                val fresca = fusedLocationClient.getCurrentLocation(request, cts.token).await()

                if (fresca != null) {
                    _userLocation.value = LatLng(fresca.latitude, fresca.longitude)
                    onResult(true)
                } else {
                    android.util.Log.w("BuyerSearchViewModel", "No se pudo obtener ubicación (null)")
                    onResult(false)
                }
            } catch (e: Exception) {
                android.util.Log.e("BuyerSearchViewModel", "Error al obtener ubicacion actual", e)
                onResult(false)
            }
        }
    }

    fun onSearchQueryChange(value: String) {
        _searchQuery.update { value }
    }

    fun agregarRestauranteAFavoritos(id: String, nombre: String, categoria: String, direccion: String) {
        viewModelScope.launch {
            val buyerId = requireBuyerId() ?: return@launch
            manageFavoriteRestaurantUseCase.add(id, buyerId, nombre, categoria, direccion)
        }
    }

    fun quitarRestauranteDeFavoritos(id: String) {
        viewModelScope.launch {
            val buyerId = requireBuyerId() ?: return@launch
            manageFavoriteRestaurantUseCase.remove(id, buyerId)
        }
    }

    fun borrarTodoElHistorial() {
        viewModelScope.launch {
            val buyerId = requireBuyerId() ?: return@launch
            clearSearchHistoryUseCase(buyerId)
        }
    }

    fun eliminarBusquedaIndividual(query: String) {
        viewModelScope.launch {
            val buyerId = requireBuyerId() ?: return@launch
            deleteSearchQueryUseCase(buyerId, query)
        }
    }

    fun ejecutarBusquedaInteligente() {
        val query = _searchQuery.value.trim()
        val queryFilter= query.lowercase(java.util.Locale.ROOT)
        if (query.isBlank()) return

        viewModelScope.launch {
            try {
                val buyerId = requireBuyerId() ?: return@launch
                val historialActual = getRecentSearchHistoryUseCase(buyerId).first()
                if (!historialActual.contains(query)) {
                    saveSearchQueryUseCase(buyerId, query)
                }
                val todosLosPlatos = getAllDishesUseCase()
                val platosFiltrados = todosLosPlatos.filter { plato ->
                    val nombrePlato = plato.name.lowercase(java.util.Locale.ROOT)
                    nombrePlato.contains(queryFilter) || calcularSimilitudTexto(nombrePlato, queryFilter) >= 0.4
                }
                _platosEncontrados.value = platosFiltrados
            } catch (e: Exception) {
                android.util.Log.e("BuyerSearchViewModel", "Error en busqueda inteligente", e)
            }
        }
    }

    fun switchToRestaurantMode(onSwitched: () -> Unit) {
        viewModelScope.launch {
            userPreferencesRepository.saveLastSelectedRole("RESTAURANT")
            onSwitched()
        }
    }

    private fun calcularDistanciaReal(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
        val radioTierra = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distanciaEnKm = radioTierra * c
        return if (distanciaEnKm < 1) "${(distanciaEnKm * 1000).toInt()} metros"
        else String.format(java.util.Locale.US, "%.1f km", distanciaEnKm)
    }

    private fun calcularSimilitudTexto(s1: String, s2: String): Double {
        val longitudMaxima = maxOf(s1.length, s2.length)
        if (longitudMaxima == 0) return 1.0
        val costo = IntArray(s2.length + 1) { it }
        for (i in 1..s1.length) {
            var anterior = costo[0]
            costo[0] = i
            for (j in 1..s2.length) {
                val temp = costo[j]
                val match = if (s1[i - 1] == s2[j - 1]) 0 else 1
                costo[j] = minOf(costo[j] + 1, costo[j - 1] + 1, anterior + match)
                anterior = temp
            }
        }
        return (longitudMaxima - costo[s2.length]).toDouble() / longitudMaxima
    }
}