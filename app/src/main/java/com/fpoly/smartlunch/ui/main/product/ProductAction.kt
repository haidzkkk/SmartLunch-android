package com.fpoly.smartlunch.ui.main.product

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import retrofit2.http.Body
import retrofit2.http.Query
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.Product


sealed class ProductAction : PolyViewAction {
    data class LikeProduct(val product: Product):ProductAction()
    object GetListProduct : ProductAction()
    object GetListTopProduct : ProductAction()
    object GetAllCategory : ProductAction()
    object GetAllFavouriteProduct : ProductAction()
    data class GetDetailProduct(val id : String) : ProductAction()
    data class GetSizeById(val id : String?) : ProductAction()
    object GetListSize : ProductAction()
    data class CreateCart(val id : String,val cart : CartRequest) : ProductAction()
    data class GetOneCartById(val id : String) : ProductAction()
    data class GetClearCart(val id : String) : ProductAction()
    data class GetChangeQuantity(val id : String, val idProduct : String , val changeQuantityRequest: ChangeQuantityRequest): ProductAction()
    data class GetRemoveProductByIdCart(val id : String , val idProduct : String, val sizeId : String): ProductAction()
    data class GetAllProductByIdCategory(val id : String): ProductAction()
    data class GetCurrentOrder( val id : String) : ProductAction()
    data class GetAllOrderByUserId(val userId : String) : ProductAction()


}