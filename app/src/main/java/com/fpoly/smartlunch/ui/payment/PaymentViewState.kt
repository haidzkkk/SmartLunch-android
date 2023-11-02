package com.fpoly.smartlunch.ui.payment

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductsResponse

data class PaymentViewState(
    var asyncProducts: Async<ProductsResponse> = Uninitialized,
    var asyncCoupons: Async<List<CouponsResponse>> = Uninitialized,
    var asyncProduct: Async<Product> = Uninitialized,
    var asyncGetOneCartById : Async<CartResponse> = Uninitialized,
    var asyncGetChangeQuantity : Async<CartResponse> = Uninitialized,
    var asyncAddOrder: Async<OrderResponse> = Uninitialized,
    var asyncApplyCoupons : Async<CartResponse> = Uninitialized,
    var asyncGetClearCart: Async<CartResponse> =Uninitialized
): MvRxState {
}