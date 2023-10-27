package com.fpoly.smartlunch.ui.main.cart

import androidx.fragment.app.Fragment
import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.ui.main.home.HomeViewEvent

sealed class CartViewEvent: PolyViewEvent {
    object testViewEvent: CartViewEvent()
    data class ReturnFragment<T: Fragment>(val fragmentClass: Class<T>): CartViewEvent()

}