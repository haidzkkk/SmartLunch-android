package com.fpoly.smartlunch.ui.main.product

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.ui.main.home.HomeViewAction

sealed class ProductAction : PolyViewAction {
    object handProduct : ProductAction()
   data class oneProduct(val id : String?) : ProductAction()
    data class oneSize(val id : String?) : ProductAction()
    object GetListSize : ProductAction()


}