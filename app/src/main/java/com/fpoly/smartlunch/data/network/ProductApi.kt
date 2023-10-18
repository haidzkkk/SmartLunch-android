package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.ProductsResponse
import io.reactivex.Observable
import retrofit2.http.GET
interface ProductApi {
    @GET("/api/products")
    fun getAllProduct(): Observable<ProductsResponse>
}