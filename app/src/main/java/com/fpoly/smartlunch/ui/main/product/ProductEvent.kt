package com.fpoly.smartlunch.ui.main.product

import androidx.fragment.app.Fragment
import com.fpoly.smartlunch.core.PolyViewEvent

sealed class ProductEvent : PolyViewEvent {
    object UpdateCart: ProductEvent()
}