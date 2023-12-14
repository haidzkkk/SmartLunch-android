package com.fpoly.smartlunch.ui.main.product

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CommentRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.PagingRequestProduct
import com.fpoly.smartlunch.data.model.Product


sealed class ProductAction : PolyViewAction {
    data class LikeProduct(val product: Product):ProductAction()
    data class GetListProductRate(val paging: PagingRequestProduct) : ProductAction()
    data class GetListProduct(val paging: PagingRequestProduct)  : ProductAction()
    data class GetListTopProduct(val paging: PagingRequestProduct)  : ProductAction()
    data class SearchProductByName(val paging: PagingRequestProduct): ProductAction()
    object GetAllCategory : ProductAction()
    object GetListCoupons : ProductAction()
    object GetAllFavouriteProduct : ProductAction()
    object GetAllHistoryProduct : ProductAction()
    data class GetDetailProduct(val id : String) : ProductAction()
    data class GetDetailCoupons(val id : String) : ProductAction()
    data class GetListSizeProduct(val idProduct : String) : ProductAction()
    data class GetListToppingProduct(val idProduct : String) : ProductAction()
    data class GetReadNotification(val id : String) : ProductAction()
    data class GetSizeById(val id : String?) : ProductAction()
    data class CreateCart(val cart : CartRequest) : ProductAction()
    object GetOneCartById: ProductAction()
    object GetClearCart : ProductAction()
    data class GetChangeQuantity(val idProduct : String , val changeQuantityRequest: ChangeQuantityRequest): ProductAction()
    data class GetRemoveProductByIdCart(val idProduct : String, val sizeId : String): ProductAction()
    data class GetAllProductByIdCategory(val id : String): ProductAction()
    data class GetCurrentOrder( val id : String) : ProductAction()
    object GetAllOrderByUserId : ProductAction()
    data class ApplyCoupon(val coupons: CouponsRequest) : ProductAction()


    data class UpdateOder(val idOder: String, val oder: OrderRequest) : ProductAction()

    data class GetListCommentsLimit(val productId : String) : ProductAction()
    data class GetListComments(val productId : String) : ProductAction()
    data class AddComment(val comment: CommentRequest, val images: List<Gallery>?) : ProductAction()

    object GetAllNotification: ProductAction()

}