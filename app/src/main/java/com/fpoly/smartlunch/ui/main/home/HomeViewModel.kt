package com.fpoly.smartlunch.ui.main.home

import android.os.Bundle
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.repository.HomeRepository
import com.fpoly.smartlunch.data.repository.PlacesRepository
import com.fpoly.smartlunch.ui.main.order.OrderDetailFragment
import com.fpoly.smartlunch.ui.main.order.TrackingOrderFragment
//import com.fpoly.smartlunch.ui.main.order.CartFragment
//import com.fpoly.smartlunch.ui.main.order.PayFragment
import com.fpoly.smartlunch.ui.main.product.ProductFragment
import com.fpoly.smartlunch.ui.main.product.ProductListFragment
import com.fpoly.smartlunch.ui.main.profile.AddressFragment
import com.fpoly.smartlunch.ui.main.profile.ChangePasswordFragment
import com.fpoly.smartlunch.ui.main.profile.EditProfileFragment
import com.fpoly.smartlunch.ui.main.profile.LanguageFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    private val placesRepository: PlacesRepository
) : PolyBaseViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {

    override fun handle(action: HomeViewAction) {
        when(action){
            is HomeViewAction.GetCurrentLocation -> handleGetCurrentLocation(action.lat,action.lon)
        }
    }

    private fun handleGetCurrentLocation(lat: Double, lon: Double) {
        setState { copy(asyncGetCurrentLocation = Loading()) }
        placesRepository.getLocationName(lat,lon)
            .execute {
                copy(asyncGetCurrentLocation = it)
            }
    }

    fun returnDetailProductFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(ProductFragment::class.java))
    }

    fun returnChangePasswordFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(ChangePasswordFragment::class.java))
    }
    fun returnEditProfileFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(EditProfileFragment::class.java))
    }fun returnAddressFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(AddressFragment::class.java))
    }
    fun returnLanguageFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(LanguageFragment::class.java))
    }
    fun returnVisibleBottomNav(isVisible: Boolean){
        _viewEvents.post(HomeViewEvent.ReturnVisibleBottomNav(isVisible))
    }
    fun handleChangeThemeMode(isChecked: Boolean) {
        _viewEvents.post(HomeViewEvent.ChangeDarkMode(isChecked))
    }
    fun returnProductListFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(ProductListFragment::class.java))
    }
    fun returnTrackingOrderFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(TrackingOrderFragment::class.java))
    }
    fun returnOrderDetailFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(OrderDetailFragment::class.java))
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: HomeViewState): HomeViewModel
    }


    companion object : MvRxViewModelFactory<HomeViewModel, HomeViewState> {
        override fun create(viewModelContext: ViewModelContext, state: HomeViewState): HomeViewModel? {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}

