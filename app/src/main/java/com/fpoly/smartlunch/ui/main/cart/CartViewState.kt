package com.fpoly.smartlunch.ui.main.cart

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.CartResponse

import com.fpoly.smartlunch.data.model.ProductsResponse

data class CartViewState(
    var test: Async<String> = Uninitialized,
    var products: Async<ProductsResponse> = Uninitialized,
    var getOneCartById : Async<CartResponse> = Uninitialized,

    ): MvRxState {
}
