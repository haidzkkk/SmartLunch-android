package com.fpoly.smartlunch.ui.main.home

import com.fpoly.smartlunch.core.PolyViewEvent

sealed class HomeViewEvent : PolyViewEvent {
    object testViewEvent: HomeViewEvent()

}