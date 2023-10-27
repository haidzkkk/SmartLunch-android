package com.fpoly.smartlunch.ui.main.cart

import android.util.Log
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.repository.HomeRepository

import com.fpoly.smartlunch.data.repository.ProductRepository
import com.fpoly.smartlunch.ui.main.home.HomeViewAction
import com.fpoly.smartlunch.ui.main.home.HomeViewEvent
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.HomeViewState
import com.fpoly.smartlunch.ui.main.product.ProductFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.Collections.copy

class CartViewModel @AssistedInject constructor(
    @Assisted state: CartViewState,
    private val repo: ProductRepository
) : PolyBaseViewModel<CartViewState, CartViewAction, CartViewEvent>(state) {

    init {
        handleGetProduct()
    }

    override fun handle(action: CartViewAction) {
        when (action) {
            is CartViewAction.GetListProduct -> handleGetProduct()
            is CartViewAction.GetOneCartById -> handleGetOneCartById(action.id)
            else -> {}
        }
    }

    private fun handleGetOneCartById(id: String?) {
        setState { copy(getOneCartById = Loading()) }
        if(id != null){
            repo.getOneCartById(id)
                .execute {
                    copy(getOneCartById = it)
                }
        }
    }

    private fun handleGetProduct() {
        setState { copy(products = Loading()) }
        repo.getProducts()
            .execute {
                copy(products = it)
            }
    }

    fun returnDetailProductFragment(){
        _viewEvents.post(CartViewEvent.ReturnFragment(ProductFragment::class.java))
    }

    fun test() = "test"

    @AssistedFactory
    interface Factory {
        fun create(initialState: CartViewState): CartViewModel
    }


    companion object : MvRxViewModelFactory<CartViewModel, CartViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: CartViewState
        ): CartViewModel? {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}