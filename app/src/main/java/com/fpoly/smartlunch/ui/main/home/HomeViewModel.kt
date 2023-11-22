package com.fpoly.smartlunch.ui.main.home

import android.os.Bundle
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.Banner
import com.fpoly.smartlunch.data.model.TokenDevice
import com.fpoly.smartlunch.data.repository.HomeRepository
import com.fpoly.smartlunch.data.repository.PlacesRepository
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.main.comment.CommentFragment
import com.fpoly.smartlunch.ui.main.coupons.CouponsDetailFragment
import com.fpoly.smartlunch.ui.main.notification.NotificationFragment
import com.fpoly.smartlunch.ui.main.order.OrderDetailFragment
import com.fpoly.smartlunch.ui.main.order.ProductReviewFragment
import com.fpoly.smartlunch.ui.main.order.TrackingOrderFragment
//import com.fpoly.smartlunch.ui.main.order.CouponsFragment
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
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.GET

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    private val placesRepository: PlacesRepository,
    private val repo: HomeRepository
) : PolyBaseViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {

    init {
        handleGetBanner()

        repo.getTokenDevice{
            repo.addTokenDevice(TokenDevice(it)).execute {
                copy()
            }
        }
    }
    override fun handle(action: HomeViewAction) {
        when(action){
            is HomeViewAction.GetCurrentLocation -> handleGetCurrentLocation(action.lat,action.lon)
            is HomeViewAction.getBanner -> handleGetBanner()
            is HomeViewAction.getDataGallery -> getDataGallery()
            else -> {}
        }
    }

    private fun handleGetCurrentLocation(lat: Double, lon: Double) {
        setState { copy(asyncGetCurrentLocation = Loading()) }
        placesRepository.getLocationName(lat,lon)
            .execute {
                copy(asyncGetCurrentLocation = it)
            }
    }

    private fun handleGetBanner() {
        setState { copy(asyncBanner = Loading()) }
        repo.getBanner().execute {
            copy(asyncBanner = it)
        }
    }

    private fun getDataGallery() {
        setState { copy(galleries = Loading()) }
        CoroutineScope(Dispatchers.Main).launch{
            val data = repo.getDataFromGallery()
            val sortData= data.sortedByDescending { it.date }.toCollection(ArrayList())
            setState { copy(galleries =  Success(sortData)) }
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
    fun returnProductReviewFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(ProductReviewFragment::class.java))
    }
    fun returnOrderDetailFragment(){
        _viewEvents.post(HomeViewEvent.ReturnFragment(OrderDetailFragment::class.java))
    }
    fun returnNotificationFragment() {
        _viewEvents.post(HomeViewEvent.ReturnFragment(NotificationFragment::class.java))
    }fun returnCouponsDetailFragment() {
        _viewEvents.post(HomeViewEvent.ReturnFragment(CouponsDetailFragment::class.java))
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

