package com.fpoly.smartlunch.ui.payment

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.District
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Province
import com.fpoly.smartlunch.data.model.ProvinceAddress
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.model.Ward

data class PaymentViewState(
    var catchError: String? = null,

    var asyncCurrentUser: Async<User> = Uninitialized,

    var curentPaymentType: Menu? = null,
    var paymentTypies:ArrayList<Menu> = arrayListOf(),

    var asyncCurentCart : Async<CartResponse> = Uninitialized,
    var asyncGetClearCart: Async<CartResponse> =Uninitialized,

    var asyncProduct: Async<Product> = Uninitialized,
    var asyncProducts: Async<ProductsResponse> = Uninitialized,
    var asyncAddOrder: Async<OrderResponse> = Uninitialized,
    var asyncUpdateOrder: Async<OrderResponse> = Uninitialized,
    var asyncUpdatePaymentOrder: Async<OrderResponse> = Uninitialized,

    var asyncCoupons: Async<List<CouponsResponse>> = Uninitialized,

    var asyncListProvince: Async<ProvinceAddress<Province>> =Uninitialized,
    var asyncListDistrict: Async<ProvinceAddress<District>> =Uninitialized,
    var asyncListWard: Async<ProvinceAddress<Ward>> =Uninitialized,
): MvRxState {
}