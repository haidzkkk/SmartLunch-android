package com.fpoly.smartlunch.ui.main.product

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.Category
import com.fpoly.smartlunch.data.model.CategoryResponse
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.Favourite
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size

data class ProductState(
    var products: Async<ProductsResponse> = Uninitialized,
    var category: Async<CategoryResponse> = Uninitialized,
    var coupons: Async<List<CouponsResponse>> = Uninitialized,
    var asyncUserCurrent: Async<ProductsResponse> = Uninitialized,
    var asyncProduct: Async<Product> = Uninitialized,
    var asynGetAllSize: Async<List<Size>> =  Uninitialized,
    var asyncGetOneSize: Async<Size> =  Uninitialized,
    var asyncCreateCart: Async<CartResponse> = Uninitialized,
    var getOneCartById : Async<CartResponse> = Uninitialized,
    var getClearCart : Async<CartResponse> = Uninitialized,
    var getChangeQuantity : Async<CartResponse> = Uninitialized,
    var getRemoveProductByIdCart : Async<CartResponse> = Uninitialized,
    var getAllProductByIdCategory : Async<List<Product>> = Uninitialized,
    var addOrder: Async<OrderResponse> = Uninitialized,
    var applyCoupons : Async<CartResponse> = Uninitialized,
    var asyncOrders: Async<List<OrderResponse>> = Uninitialized,
    var asyncOrderings: Async<List<OrderResponse>> = Uninitialized,
    var asyncLikeProduct: Async<Favourite> = Uninitialized,
    var asyncGetFavourite: Async<Favourite> = Uninitialized,
    var asyncFavourites: Async<List<Product>> = Uninitialized,
    var asyncTopProduct: Async<List<Product>> = Uninitialized,
    ): MvRxState {

        var isSwipeLoading = products is Loading || asyncTopProduct is Loading
}
