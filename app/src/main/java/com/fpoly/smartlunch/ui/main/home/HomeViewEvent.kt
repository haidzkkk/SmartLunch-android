package com.fpoly.smartlunch.ui.main.home

import androidx.fragment.app.Fragment
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.ui.main.product.ProductFragment
import com.fpoly.smartlunch.ui.security.SecurityViewEvent

sealed class HomeViewEvent : PolyViewEvent {
    data class ReturnVisibleBottomNav(val isVisibleBottomNav: Boolean): HomeViewEvent()
    data class ReturnFragment<T: Fragment>(val fragmentClass: Class<T>): HomeViewEvent()
}