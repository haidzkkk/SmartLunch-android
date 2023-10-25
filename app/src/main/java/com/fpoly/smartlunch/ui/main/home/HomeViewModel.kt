package com.fpoly.smartlunch.ui.main.home

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.repository.HomeRepository
import com.fpoly.smartlunch.ui.main.product.ProductFragment
import com.fpoly.smartlunch.ui.main.profile.ChangePasswordFragment
import com.fpoly.smartlunch.ui.main.profile.EditProfileFragment
import com.fpoly.smartlunch.ui.main.profile.LanguageFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    private val repo: HomeRepository
) : PolyBaseViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {

    override fun handle(action: HomeViewAction) {
        when(action){
            else -> {}
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

    fun test() = "test"

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

