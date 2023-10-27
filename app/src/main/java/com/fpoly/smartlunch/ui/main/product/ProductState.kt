package com.fpoly.smartlunch.ui.main.product

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.Category
import com.fpoly.smartlunch.data.model.CategoryResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size

data class ProductState(
    var products: Async<ProductsResponse> = Uninitialized,
    var category: Async<CategoryResponse> = Uninitialized,
    var asyncUserCurrent: Async<ProductsResponse> = Uninitialized,
    var product: Async<Product> = Uninitialized,
    var size: Async<List<Size>> =  Uninitialized,
    var oneSize: Async<Size> =  Uninitialized,
    var asyncCreateCart: Async<CartResponse> = Uninitialized,
    var getOneCartById : Async<CartResponse> = Uninitialized,
    var getClearCart : Async<CartResponse> = Uninitialized,
    var getChangeQuantity : Async<CartResponse> = Uninitialized,
    var getRemoveProductByIdCart : Async<CartResponse> = Uninitialized,
    var getAllProductByIdCategory : Async<List<Product>> = Uninitialized,

    ): MvRxState {
}
