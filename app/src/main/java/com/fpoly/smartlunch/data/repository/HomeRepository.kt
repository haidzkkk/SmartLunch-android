package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.network.ProductApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.random.Random

class HomeRepository @Inject constructor(
    private val api: ProductApi
) {
    fun test(): Observable<String> {
        return Observable.just("Repository test")
    }
}