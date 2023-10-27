package com.fpoly.smartlunch.ui.main.love

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.repository.ProductRepository
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductEvent
import com.fpoly.smartlunch.ui.main.product.ProductState
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FavouriteViewModel @AssistedInject constructor(
    @Assisted state: FavouriteViewState,
    private val repository: ProductRepository
): PolyBaseViewModel<FavouriteViewState, FavouriteViewAction, FavouriteViewEvent>(state) {
    override fun handle(action: FavouriteViewAction) {
        when(action){
            is FavouriteViewAction.GetListFavourite -> handleGetAllFavorite()
            else -> {
            }
        }
    }

    private fun handleGetAllFavorite(){
        setState { copy(Favourite = Loading()) }
        repository.getAllFavourite()
            .execute {
                copy(Favourite = it)
            }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: FavouriteViewState): FavouriteViewModel
    }

    companion object : MvRxViewModelFactory<FavouriteViewModel, FavouriteViewState> {
        override fun create(viewModelContext: ViewModelContext, state: FavouriteViewState): FavouriteViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}