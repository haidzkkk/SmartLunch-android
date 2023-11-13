package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.Banner
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.network.ContentDataSource
import com.fpoly.smartlunch.data.network.ProductApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class HomeRepository @Inject constructor(
    private val api: ProductApi,
    private val contentDataSource: ContentDataSource
) {
    fun test(): Observable<String> {
        return Observable.just("Repository test")
    }

    fun getBanner(): Observable<ArrayList<Banner>> = api.getBanner().subscribeOn(Schedulers.io())

    suspend fun getDataFromGallery(): ArrayList<Gallery> {
        return withContext(Dispatchers.IO){
            contentDataSource.getImage()
        }
    }
}