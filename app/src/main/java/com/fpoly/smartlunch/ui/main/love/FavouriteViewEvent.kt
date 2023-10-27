package com.fpoly.smartlunch.ui.main.love

import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.ui.main.product.ProductEvent

sealed class FavouriteViewEvent : PolyViewEvent{

    object handFavorite : FavouriteViewEvent()
}