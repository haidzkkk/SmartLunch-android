package com.fpoly.smartlunch.data.repository

import android.annotation.SuppressLint
import com.fpoly.smartlunch.data.model.Banner
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.TokenDevice
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.network.AuthApi
import com.fpoly.smartlunch.data.network.ContentDataSource
import com.fpoly.smartlunch.data.network.ProductApi
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class HomeRepository @Inject constructor(
    private val userApi: AuthApi,
    private val productApi: ProductApi,
    private val contentDataSource: ContentDataSource
) {
    fun test(): Observable<String> {
        return Observable.just("Repository test")
    }

    fun getBanner(): Observable<ArrayList<Banner>> = productApi.getBanner().subscribeOn(Schedulers.io())

    fun getTokenDevice(callBack: (tokenDeivce: String) -> Unit){
        FirebaseMessaging.getInstance().token.addOnCompleteListener{
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            callBack(it.result)
        }
    }

    fun addTokenDevice(tokenDevice: TokenDevice): Observable<User> =  userApi.updateTokenDevice(tokenDevice).subscribeOn(Schedulers.io())

    suspend fun getDataFromGallery(): ArrayList<Gallery> {
        return withContext(Dispatchers.IO){
            contentDataSource.getImage()
        }
    }
}