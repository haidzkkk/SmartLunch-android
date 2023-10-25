package com.fpoly.smartlunch.ui.main.profile

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.User

data class UserViewState(
    var asyncCurrentUser: Async<User> = Uninitialized,
    var asyncUserSearching: Async<User> = Uninitialized,
    var asyncUpdateUser: Async<User> = Uninitialized,
    var asyncChangePassword: Async<TokenResponse> = Uninitialized,
): MvRxState {

}