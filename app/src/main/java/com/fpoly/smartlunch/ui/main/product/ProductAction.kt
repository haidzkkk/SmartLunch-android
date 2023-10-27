package com.fpoly.smartlunch.ui.main.product

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import retrofit2.http.Body
import retrofit2.http.Query
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.OrderRequest


sealed class ProductAction : PolyViewAction {
    object GetListProduct : ProductAction()

    object GetListCategory : ProductAction()
    object handProduct : ProductAction()
    data class oneProduct(val id : String?) : ProductAction()
    data class oneSize(val id : String?) : ProductAction()
    object GetListSize : ProductAction()
    data class CreateCart(val id : String,val cart : CartRequest) : ProductAction()
    data class GetOneCartById(val id : String) : ProductAction()
    data class GetClearCart(val id : String) : ProductAction()
    data class GetChangeQuantity(val id : String, val idProduct : String , val changeQuantityRequest: ChangeQuantityRequest): ProductAction()
    data class getRemoveProductByIdCart(val id : String , val idProduct : String, val sizeId : String): ProductAction()
    data class getAllProductByIdCategory(val id : String): ProductAction()
    data class incrementViewProduct(val id : String): ProductAction()
    data class CreateOder( val oder : OrderRequest) : ProductAction()
    object GetListCoupons : ProductAction()
    data class applyCoupon(val id : String,val coupons : CouponsRequest) : ProductAction()

}