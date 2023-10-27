package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.ProductOrder
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size

import com.fpoly.smartlunch.data.network.ProductApi

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.random.Random


class ProductRepository @Inject constructor(
    private val api: ProductApi
) {
    private var number = Random.nextInt()

    fun test(): Observable<String> {
        Thread.sleep(2000)
        return Observable.just("Repository test: $number")
    }

    fun getProducts(): Observable<ProductsResponse> = api.getAllProduct().subscribeOn(Schedulers.io())
    fun getOneProducts(id : String): Observable<Product> = api.getOneProduct(id).subscribeOn(Schedulers.io())
    fun getOneSize(id : String): Observable<Size> = api.getOneSize(id).subscribeOn(Schedulers.io())
    fun getSize(): Observable<List<Size>> = api.getAllSize().subscribeOn(Schedulers.io())
    fun getCreateCart(id :String,cart: CartRequest): Observable<CartResponse> = api.getCreateCart(id,cart).subscribeOn(Schedulers.io())
    fun getOneCartById(id : String): Observable<CartResponse> = api.getOneCartById(id).subscribeOn(Schedulers.io())
    fun getClearCart(id :String): Observable<CartResponse> = api.getClearCart(id).subscribeOn(Schedulers.io())
    fun getChangeQuantityCart(id : String) : Observable<CartResponse> = api.getChangeQuantityCart(id).subscribeOn(Schedulers.io())
    fun createOrder(order: OrderRequest)=api.createOrder(order).subscribeOn(Schedulers.io())
    fun getCoupons(): Observable<List<CouponsResponse>> = api.getAllCoupons().subscribeOn(Schedulers.io())


    fun applyCoupon(id :String,coupons:CouponsRequest): Observable<CartResponse> = api.applyCoupon(id,coupons).subscribeOn(Schedulers.io())


}