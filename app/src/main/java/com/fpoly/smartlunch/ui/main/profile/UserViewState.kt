package com.fpoly.smartlunch.ui.main.profile

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.Address
import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.District
import com.fpoly.smartlunch.data.model.Province
import com.fpoly.smartlunch.data.model.ProvinceAddress
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.model.Ward

data class UserViewState(
    var asyncLogout: Async<User> = Uninitialized,
    var asyncCurrentUser: Async<User> = Uninitialized,
    var asyncUserSearching: Async<User> = Uninitialized,
    var asyncUpdateUser: Async<User> = Uninitialized,

    var asyncListAddress: Async<List<Address>> = Uninitialized,
    var asyncAddress: Async<Address> = Uninitialized,
    var asyncCreateAddress: Async<Address> = Uninitialized,
    var asyncUpdateAddress: Async<Address> = Uninitialized,
    var asyncDeleteAddress: Async<Address> = Uninitialized,

    var asyncAddressAdmin: Async<Address> = Uninitialized,

    var asyncChangePassword: Async<TokenResponse> = Uninitialized,

    var asyncListProvince: Async<ProvinceAddress<Province>> =Uninitialized,
    var asyncListDistrict: Async<ProvinceAddress<District>> =Uninitialized,
    var asyncListWard: Async<ProvinceAddress<Ward>> =Uninitialized,
): MvRxState {

}