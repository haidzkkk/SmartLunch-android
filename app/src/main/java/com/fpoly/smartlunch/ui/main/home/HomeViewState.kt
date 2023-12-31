package com.fpoly.smartlunch.ui.main.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.OpenStreetMapAddress
import com.fpoly.smartlunch.data.model.OpenStreetMapResponse
import com.fpoly.smartlunch.data.model.Banner
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.User

data class HomeViewState(
    var users : Async<List<User>> = Uninitialized,
    var asyncGetCurrentLocation: Async<OpenStreetMapResponse> = Uninitialized,
    var asyncBanner : Async<ArrayList<Banner>> = Uninitialized,
    val galleries: Async<ArrayList<Gallery>> = Uninitialized,
): MvRxState {
}