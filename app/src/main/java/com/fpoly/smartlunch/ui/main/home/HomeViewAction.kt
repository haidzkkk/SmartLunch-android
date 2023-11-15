package com.fpoly.smartlunch.ui.main.home

import com.fpoly.smartlunch.core.PolyViewAction

sealed class HomeViewAction: PolyViewAction {
    data class GetCurrentLocation(val lat: Double,val lon: Double) : HomeViewAction()
}