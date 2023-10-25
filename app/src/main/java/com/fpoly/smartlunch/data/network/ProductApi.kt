package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductApi {
    @GET("/api/products")
    fun getAllProduct(): Observable<ProductsResponse>
    @GET("/api/products/{id}")
    fun getOneProduct(@Path("id")id: String): Observable<Product>
    @GET("/api/size/{id}")
    fun getOneSize(@Path("id")id: String): Observable<Size>
    @GET("/api/size")
    fun getAllSize(): Observable<List<Size>>
    @POST("/api/carts/{id}/create")
    fun getCreateCart(@Path("id") id: String, @Body cart: CartRequest): Observable<CartResponse>
    @GET("/api/carts/{id}")
    fun getOneCartById(@Path("id")id: String): Observable<CartResponse>
    @DELETE("/api/carts/{id}/clears")
    fun getClearCart(@Path("id") id: String): Observable<CartResponse>

    @DELETE("/api/carts/{id}/change")
    fun getChangeQuantityCart(@Path("id") id: String): Observable<CartResponse>


}