package com.labajada.app.presentation.buyer.search

data class BuyerSearchState(
    val showProfileSheet: Boolean = false,
    val showMenuSheet: Boolean = false,
    val profileCurrentSection: String = "MENU",
    val selectedDishForCart: com.labajada.app.domain.model.Dish? = null,
    val huariqueParaMenu: RadarHuarique? = null,
    val hasLocationPermission: Boolean = false,
    val isLoadingLocation: Boolean = false,
    val locationTrigger: Int = 0
)