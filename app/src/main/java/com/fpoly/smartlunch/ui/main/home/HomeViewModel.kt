package com.fpoly.smartlunch.ui.main.home

import android.os.Bundle
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.TokenDevice
import com.fpoly.smartlunch.data.repository.HomeRepository
import com.fpoly.smartlunch.data.repository.PlacesRepository
import com.fpoly.smartlunch.ui.main.comment.CommentFragment
import com.fpoly.smartlunch.ui.main.coupons.CouponsDetailFragment
import com.fpoly.smartlunch.ui.main.coupons.CouponsFragment
import com.fpoly.smartlunch.ui.main.love.FavouriteFragment
import com.fpoly.smartlunch.ui.main.notification.NotificationFragment
import com.fpoly.smartlunch.ui.main.order.OrderDetailFragment
import com.fpoly.smartlunch.ui.main.order.OrderFragment
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
import com.fpoly.smartlunch.ui.main.profile.ProfileFragment
import com.fpoly.smartlunch.ui.main.search.SearchFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    fun returnCommentFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(CommentFragment::class.java))
    }

    fun returnDetailProductFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(ProductFragment::class.java))
    }

    fun returnChangePasswordFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(ChangePasswordFragment::class.java))
    }
    fun returnEditProfileFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(EditProfileFragment::class.java))
    }fun returnAddressFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(AddressFragment::class.java))
    }
    fun returnLanguageFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(LanguageFragment::class.java))
    }
    fun returnVisibleBottomNav(isVisible: Boolean){
        _viewEvents.post(HomeViewEvent.ReturnVisibleBottomNav(isVisible))
    }
    fun handleChangeThemeMode(isChecked: Boolean) {
        _viewEvents.post(HomeViewEvent.ChangeDarkMode(isChecked))
    }

    fun returnHomeFragment() {
        _viewEvents.post(HomeViewEvent.ReturnFragment(HomeFragment::class.java))
    }fun returnFavouriteFragment() {
        _viewEvents.post(HomeViewEvent.ReturnFragment(FavouriteFragment::class.java))
    }fun returnCouponsFragment() {
        _viewEvents.post(HomeViewEvent.ReturnFragment(CouponsFragment::class.java))
    }fun returnOrderFragment() {
        _viewEvents.post(HomeViewEvent.ReturnFragment(OrderFragment::class.java))
    }fun returnProfileFragment() {
        _viewEvents.post(HomeViewEvent.ReturnFragment(ProfileFragment::class.java))
    }

    fun returnProductListFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(ProductListFragment::class.java))
    }
    fun returnTrackingOrderFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(TrackingOrderFragment::class.java))
    }
    fun returnProductReviewFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(ProductReviewFragment::class.java))
    }
    fun returnOrderDetailFragment(){
        _viewEvents.post(HomeViewEvent.NavigateTo(OrderDetailFragment::class.java))
    }
    fun returnNotificationFragment() {
        _viewEvents.post(HomeViewEvent.NavigateTo(NotificationFragment::class.java))
    }
    fun setBadgeBottomNav(idItemMenu: Int, position: Int?) {
        _viewEvents.post(HomeViewEvent.SetBadgeBottomNav(idItemMenu, position))
    }
    fun returnCouponsDetailFragment() {
        _viewEvents.post(HomeViewEvent.NavigateTo(CouponsDetailFragment::class.java))
    }
    fun returnSearchFragment(bundle: Bundle? = null) {
        _viewEvents.post(HomeViewEvent.NavigateTo(SearchFragment::class.java, bundle))
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

