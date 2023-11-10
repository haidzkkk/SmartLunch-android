package com.fpoly.smartlunch.ui.main.profile

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.Address
import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.User

data class UserViewState(
    var asyncCurrentUser: Async<User> = Uninitialized,
    var asyncUserSearching: Async<User> = Uninitialized,
    var asyncUpdateUser: Async<User> = Uninitialized,
    var asyncListAddress: Async<List<Address>> = Uninitialized,
    var asyncAddress: Async<Address> = Uninitialized,
    var asyncCreateAddress: Async<Address> = Uninitialized,
    var asyncUpdateAddress: Async<Address> = Uninitialized,
    var asyncDeleteAddress: Async<Address> = Uninitialized,
    var asyncChangePassword: Async<TokenResponse> = Uninitialized,
): MvRxState {

}