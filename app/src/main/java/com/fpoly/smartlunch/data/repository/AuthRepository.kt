package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.network.AuthApi
import com.fpoly.smartlunch.ultis.getFakeTokenLogin
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class AuthRepository(
    private val api: AuthApi,
) {
    fun login(userName: String, password: String): Observable<TokenResponse>
            = getFakeTokenLogin().subscribeOn(Schedulers.io())
}