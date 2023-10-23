package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.ProductsResponse
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
}