package com.fpoly.smartlunch.ui.main.home

import androidx.fragment.app.Fragment

import com.fpoly.smartlunch.core.PolyViewEvent


sealed class HomeViewEvent : PolyViewEvent {
    data class ReturnVisibleBottomNav(val isVisibleBottomNav: Boolean): HomeViewEvent()
    data class ReturnFragment<T: Fragment>(val fragmentClass: Class<T>): HomeViewEvent()
}