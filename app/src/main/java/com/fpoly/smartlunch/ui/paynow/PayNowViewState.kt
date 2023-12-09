package com.fpoly.smartlunch.ui.paynow

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

data class PayNowViewState(
    val curentCart : Async<CartResponse> = Uninitialized,
    val currentCoupon : Async<CouponsResponse> = Uninitialized,
    var asyncAddOrder: Async<OrderResponse> = Uninitialized,

): MvRxState {
}