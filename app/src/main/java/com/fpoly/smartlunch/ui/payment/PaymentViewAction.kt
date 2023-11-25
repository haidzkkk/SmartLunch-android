package com.fpoly.smartlunch.ui.payment

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.ui.main.product.ProductAction

sealed class PaymentViewAction : PolyViewAction {
    object getListPaymentType: PaymentViewAction()
    data class setCurrentPaymentType(val menu: Menu) : PaymentViewAction()

    object GetListProduct : PaymentViewAction()
    data class GetDetailProduct(val id: String) : PaymentViewAction()
    data class IncrementViewProduct(val id: String) : PaymentViewAction()

    object GetClearCart : PaymentViewAction()
    object GetOneCartById : PaymentViewAction()
    data class GetChangeQuantity(val idProduct: String, val changeQuantityRequest: ChangeQuantityRequest) : PaymentViewAction()

    data class CreateOder(val oder: OrderRequest) : PaymentViewAction()
    data class UpdateOder(val idOder: String, val oder: OrderRequest) : PaymentViewAction()
    data class UpdateIsPaymentOder(val id: String, val isPayment: Boolean) : PaymentViewAction()

    object GetListCoupons : PaymentViewAction()
    data class ApplyCoupon(val coupons: CouponsRequest) : PaymentViewAction()

    object GetProvinceAddress : PaymentViewAction()
    data class GetDistrictAddress(val provinceId: String) : PaymentViewAction()
    data class GetWardAddress(val districtId: String) : PaymentViewAction()
}