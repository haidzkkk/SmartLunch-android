package com.fpoly.smartlunch.ui.main.home

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewAction

sealed class HomeViewAction: PolyViewAction {
    data class GetCurrentLocation(val lat: Double,val lon: Double) : HomeViewAction()
    object getBanner: HomeViewAction()
    object getDataGallery: HomeViewAction()
}