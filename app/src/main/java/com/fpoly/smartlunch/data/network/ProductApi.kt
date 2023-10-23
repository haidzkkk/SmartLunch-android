package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size
import io.reactivex.Observable
import retrofit2.http.GET
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


}