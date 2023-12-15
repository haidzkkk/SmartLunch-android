package com.fpoly.smartlunch.ui.main.product

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.Category
import com.fpoly.smartlunch.data.model.CategoryResponse
import com.fpoly.smartlunch.data.model.Comment
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.Favourite
import com.fpoly.smartlunch.data.model.Notification
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductOrder
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.data.model.Topping

data class ProductState(
    var coupons: Async<ArrayList<CouponsResponse>> = Uninitialized,

    var productsRate: Async<ProductsResponse> = Uninitialized,
    var catchError: String? = null,
    var products: Async<ProductsResponse> = Uninitialized,
    var category: Async<CategoryResponse> = Uninitialized,
    var asyncCoupons: Async<ArrayList<CouponsResponse>> = Uninitialized,
    var asyncOneCoupons: Async<CouponsResponse> = Uninitialized,
    var asyncCurentCart : Async<CartResponse> = Uninitialized,

    var asyncUserCurrent: Async<ProductsResponse> = Uninitialized,
    var asyncTopProduct: Async<ProductsResponse> = Uninitialized,
    var asyncProducts: Async<ProductsResponse> = Uninitialized,

    var asyncProduct: Async<Product> = Uninitialized,
    var asynGetSizeProduct: Async<ArrayList<Size>> =  Uninitialized,
    var asyncGetOneSize: Async<Size> =  Uninitialized,

    var asyncToppingsProduct: Async<ArrayList<Topping>> =  Uninitialized,

    var curentCartResponse: Async<CartResponse> = Uninitialized,
    var curentAddProductToCartResponse: Async<CartResponse> = Uninitialized,
    var getOneCartById : Async<CartResponse> = Uninitialized,
    var getClearCart : Async<CartResponse> = Uninitialized,
    var getChangeQuantity : Async<CartResponse> = Uninitialized,
    var getRemoveProductByIdCart : Async<CartResponse> = Uninitialized,
    var applyCoupons : Async<CartResponse> = Uninitialized,

    var asyncCommentsLimit: Async<ArrayList<Comment>> = Uninitialized,
    var asyncComments: Async<ArrayList<Comment>> = Uninitialized,
    var asyncAddComment: Async<Comment> = Uninitialized,

    var getAllProductByIdCategory : Async<ArrayList<Product>> = Uninitialized,

    var addOrder: Async<OrderResponse> = Uninitialized,
    var asyncUnconfirmed: Async<List<OrderResponse>> = Uninitialized,
    var asyncConfirmed: Async<List<OrderResponse>> = Uninitialized,
    var asyncDelivering: Async<List<OrderResponse>> = Uninitialized,
    var asyncCancelled: Async<List<OrderResponse>> = Uninitialized,
    var asyncCompleted: Async<List<OrderResponse>> = Uninitialized,
    var asyncUpdateOrder: Async<OrderResponse> = Uninitialized,

    var asyncLikeProduct: Async<Favourite> = Uninitialized,
    var asyncGetFavourite: Async<Favourite> = Uninitialized,
    var asyncFavourites: Async<ArrayList<Product>> = Uninitialized,
    var asyncHistories: Async<ArrayList<ProductOrder>> = Uninitialized,
    var asyncNotifications: Async<List<Notification>> = Uninitialized,
    var asyncUnreadNotifications: Async<List<Notification>> = Uninitialized,
    var asyncReadNotification: Async<Notification> = Uninitialized,
    var currentProductsSearch: Async<ProductsResponse> = Uninitialized
    ): MvRxState {
        var isSwipeLoading = productsRate is Loading || asyncTopProduct is Loading
}
