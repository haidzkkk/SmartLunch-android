package com.fpoly.smartlunch.ui.main.cart

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.ui.main.home.HomeViewAction
import com.fpoly.smartlunch.ui.main.product.ProductAction

sealed class CartViewAction : PolyViewAction {
//    object TestViewAction: CartViewAction()
//      object GetListOder : CartViewAction()
    object GetListProduct : CartViewAction()
    data class oneProduct(val id : String?) : CartViewAction()
    data class GetOneCartById(val id : String) : CartViewAction()

}