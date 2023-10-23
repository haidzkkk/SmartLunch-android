package com.fpoly.smartlunch.ui.main.product

import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.ui.security.LoginViewEvent

sealed class ProductEvent : PolyViewEvent {
    object handProduct : ProductEvent()
    object ReturnFragmentViewEvent : ProductEvent()
    data class ToFragmentViewEvent(val id : Int) : ProductEvent()
}