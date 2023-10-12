package com.fpoly.smartlunch.ultis

import com.fpoly.smartlunch.data.model.TokenResponse
import io.reactivex.Observable

fun getFakeTokenLogin(): Observable<TokenResponse> {
    return Observable.just(TokenResponse("accessToken", "refreshTOken"))
}