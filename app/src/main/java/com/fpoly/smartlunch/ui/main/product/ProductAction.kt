package com.fpoly.smartlunch.ui.main.product

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.CartRequest

sealed class ProductAction : PolyViewAction {
    object GetListProduct : ProductAction()
    object handProduct : ProductAction()
    data class oneProduct(val id : String?) : ProductAction()
    data class oneSize(val id : String?) : ProductAction()
    object GetListSize : ProductAction()
    data class CreateCart(val id : String,val cart : CartRequest) : ProductAction()
    data class GetOneCartById(val id : String) : ProductAction()

    data class GetClearCart(val id : String) : ProductAction()



}