package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.network.UserApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.random.Random

class HomeRepository @Inject constructor(
    private val api: UserApi
) {

    fun test(): Observable<String> {
        return Observable.just("Repository test")
    }

    fun getUsers(): Observable<List<User>> =
        api.getUser().subscribeOn(Schedulers.io())
}