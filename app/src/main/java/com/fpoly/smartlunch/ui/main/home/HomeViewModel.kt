package com.fpoly.smartlunch.ui.main.home

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.repository.HomeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.schedulers.Schedulers

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    private val repo: HomeRepository
) : PolyBaseViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {

    override fun handle(action: HomeViewAction) {
        when(action){
            is HomeViewAction.GetListProduct -> handleGetProduct()
          //  is HomeViewAction.GetUserViewAction -> handleGetUsers()
        }
    }

    private fun handleGetProduct() {

        setState { copy(products = Loading()) }
        repo.getProducts()
            .execute {
                copy(products = it)
            }
    }



    fun testEvent(){
        _viewEvents.post(HomeViewEvent.testViewEvent)
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

