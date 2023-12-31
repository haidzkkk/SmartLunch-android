package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.Banner
import com.fpoly.smartlunch.data.model.CartLocalRequest
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.CategoryResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.Favourite
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductOrder
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.data.model.Topping
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("/api/products")
    fun getAllProduct(@Query("_limit") _limit: Int?,
                      @Query("_sort") _sort: String?,
                      @Query("_order") _order: String?,
                      @Query("_page") _page: Int?,
                      @Query("q") query: String?,
                      ): Observable<ProductsResponse>
    @GET("/api/products/{id}")
    fun getOneProduct(@Path("id")id: String): Observable<Product>
    @GET("/api/size/{id}")
    fun getOneSize(@Path("id")id: String): Observable<Size>
    @GET("/api/size/product/{id}")
    fun getSizeProduct(@Path("id") id : String): Observable<ArrayList<Size>>
    @GET("/api/topping/product/{id}")
    fun getToppingProduct(@Path("id") id : String): Observable<ArrayList<Topping>>
    @POST("/api/carts/create")
    fun getCreateCart(@Body cart: CartRequest): Observable<CartResponse>
    @GET("/api/carts")
    fun getOneCartById(): Observable<CartResponse>
    @DELETE("/api/carts/clears")
    fun getClearCart(): Observable<CartResponse>
    @PUT("/api/carts/change")
    fun getChangeQuantityCart(@Query("idProduct") idProduct: String,@Body changeQuantityRequest: ChangeQuantityRequest): Observable<CartResponse>
    @PUT("/api/carts/local/change")
    fun getChangeQuantityCartLocal(@Query("idProduct") idProduct: String,@Body cartLocalRequest: CartLocalRequest<ChangeQuantityRequest>): Observable<CartResponse>
    @PUT("/api/carts/local/update")
    fun getCheckUpdateCartLocal(@Body cartLocalRequest: CartLocalRequest<ChangeQuantityRequest>): Observable<CartResponse>
    @DELETE("/api/carts/remove")
    fun getRemoveGetOneProductCart(@Query("idProduct") idProduct: String,@Query("sizeId") sizeId: String ): Observable<CartResponse>
    @PATCH("/api/carts/apply")
    fun applyCoupon(@Body coupons: CouponsRequest): Observable<CartResponse>
    @PATCH("/api/carts/local/apply")
    fun applyCouponLocal(@Body cartLocalRequest: CartLocalRequest<CouponsRequest>): Observable<CartResponse>

    @GET("/api/category")
    fun getAllCategory(): Observable<CategoryResponse>
    @GET("/api/topViewedProducts")
    fun getTopViewedProducts(): Observable<ArrayList<Product>>
    @GET("/api/category/products/{categoryId}")
    fun getAllProductByIdCategory(@Path("categoryId") id: String): Observable<ArrayList<Product>>
    @GET("/api/products/views/{id}")
    fun getViewProduct(@Path("id") id: String) : Completable
    @GET("/api/favourite")
    fun getAllFavourite(): Observable<ArrayList<Product>>
    @GET("/api/order/user/history")
    fun getAllHistory(): Observable<ArrayList<ProductOrder>>
    @GET("/api/favourite/product/{id}")
    fun getFavourite(@Path("id") id: String): Observable<Favourite>
    @POST("/api/favourite")
    fun likeProduct(@Body product: Product): Observable<Favourite>
    @POST("/api/order")
    fun createOrder( @Body order: OrderRequest): Observable<OrderResponse>
    @POST("/api/order/local")
    fun createOrderCartLocal( @Body cartLocalRequest: CartLocalRequest<OrderRequest>): Observable<OrderResponse>
    @PATCH("/api/order/{id}")
    fun updateOrder(@Path("id") id: String, @Body order: OrderRequest): Observable<OrderResponse>
    @PATCH("/api/order/payment/{id}")
    fun updateIsPaymentOrder(@Path("id") id: String, @Query("isPayment") isPayment: Boolean): Observable<OrderResponse>
    @GET("/api/coupons")
    fun getAllCoupons(): Observable<ArrayList<CouponsResponse>>
    @GET("/api/coupons/{id}")
    fun getOneCoupons(@Path("id") id: String): Observable<CouponsResponse>
    @GET("/api/userId/order")
    fun getAllOrderByUserId(
        @Query("statusId") statusId: String
    ): Observable<ArrayList<OrderResponse>>
    @GET("/api/order/{id}")
    fun getCurrentOrder(@Path("id") id: String): Observable<OrderResponse>
    @GET("/api/banner")
    fun getBanner(): Observable<ArrayList<Banner>>

    @GET("api/products/search/{text}")
    fun searchProductByName(@Path("text") text: String):Observable<ArrayList<Product>>




}