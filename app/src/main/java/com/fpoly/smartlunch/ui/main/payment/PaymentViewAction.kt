package com.fpoly.smartlunch.ui.main.payment

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.ui.main.product.ProductAction

sealed class PaymentViewAction : PolyViewAction {
    data class GetClearCart(val id : String) : PaymentViewAction()
    object GetListProduct : PaymentViewAction()
    data class GetDetailProduct(val id: String) : PaymentViewAction()
    data class GetOneCartById(val id: String) : PaymentViewAction()
    data class GetChangeQuantity(
        val id: String,
        val idProduct: String,
        val changeQuantityRequest: ChangeQuantityRequest
    ) : PaymentViewAction()

    data class IncrementViewProduct(val id: String) : PaymentViewAction()
    data class CreateOder(val oder: OrderRequest) : PaymentViewAction()
    object GetListCoupons : PaymentViewAction()
    data class ApplyCoupon(val id: String, val coupons: CouponsRequest) : PaymentViewAction()
}