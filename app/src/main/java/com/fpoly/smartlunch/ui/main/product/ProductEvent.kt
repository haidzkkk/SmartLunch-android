package com.fpoly.smartlunch.ui.main.product

import androidx.fragment.app.Fragment
import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.ui.main.home.HomeViewEvent

sealed class ProductEvent : PolyViewEvent {
    data class ReturnVisibleBottomNav(val isVisibleBottomNav: Boolean): ProductEvent()
    data class ReturnFragment<T: Fragment>(val fragmentClass: Class<T>): ProductEvent()
}