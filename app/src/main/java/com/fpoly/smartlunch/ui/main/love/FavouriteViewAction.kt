package com.fpoly.smartlunch.ui.main.love

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.ui.main.product.ProductAction

sealed class FavouriteViewAction : PolyViewAction {
    object GetListFavourite : FavouriteViewAction()
}