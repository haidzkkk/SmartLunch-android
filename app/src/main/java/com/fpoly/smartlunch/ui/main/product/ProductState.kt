package com.fpoly.smartlunch.ui.main.product

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size

data class ProductState(
    var asyncUserCurrent: Async<ProductsResponse> = Uninitialized,
    var product: Async<Product> = Uninitialized,
    var size: Async<List<Size>> =  Uninitialized,
    var oneSize: Async<Size> =  Uninitialized,

    ): MvRxState {
}
