package com.fpoly.smartlunch.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment

import com.fpoly.smartlunch.core.PolyViewEvent


sealed class HomeViewEvent : PolyViewEvent {
    data class ChangeDarkMode(var isCheckedDarkMode: Boolean) : HomeViewEvent()
    data class ReturnVisibleBottomNav(val isVisibleBottomNav: Boolean): HomeViewEvent()
    data class ReturnFragment<T: Fragment>(val fragmentClass: Class<T>): HomeViewEvent()
    data class NavigateTo<T: Fragment>(val fragmentClass: Class<T>, val bundle: Bundle? = null): HomeViewEvent()
    data class ReturnFragmentWithArgument<T: Fragment>(val fragmentClass: Class<T>, val bundle: Bundle): HomeViewEvent()
    data class SetBadgeBottomNav(val id: Int, val position: Int?): HomeViewEvent()

}