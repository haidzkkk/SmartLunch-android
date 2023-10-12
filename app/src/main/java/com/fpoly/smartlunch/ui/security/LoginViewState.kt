package com.fpoly.smartlunch.ui.security

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.User

data class LoginViewState(
    var tokenFcm: String = "",
    var asyncLogin: Async<TokenResponse> = Uninitialized,
    var asyncUserCurrent:Async<User> = Uninitialized
): MvRxState {
    fun isLoading() = asyncLogin is Loading ||  asyncUserCurrent is Loading
}