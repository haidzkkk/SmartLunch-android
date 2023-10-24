package com.fpoly.smartlunch.ui.main.product

import com.fpoly.smartlunch.core.PolyViewEvent

sealed class ProductEvent : PolyViewEvent {
    object handProduct : ProductEvent()
    object ReturnFragmentViewEvent : ProductEvent()
    data class ToFragmentViewEvent(val id : Int) : ProductEvent()
}