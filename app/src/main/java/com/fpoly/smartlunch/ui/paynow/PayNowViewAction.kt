package com.fpoly.smartlunch.ui.paynow

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.ui.payment.PaymentViewAction

sealed class PayNowViewAction : PolyViewAction {
    data class SetCurrentCart(val cartResponse: CartResponse): PayNowViewAction()
    data class SetCupontCart(val currentCart: CartResponse?, val coupons: CouponsResponse): PayNowViewAction()
    data class ChangeQuantityProduct(val currentCart: CartResponse?, val idProduct: String, val changeQuantityRequest: ChangeQuantityRequest): PayNowViewAction()
    data class CheckUpdateCartLocal(val currentCart: CartResponse?): PayNowViewAction()
    data class CreateOder(val currentCart: CartResponse?, val orderRequest: OrderRequest): PayNowViewAction()

}
