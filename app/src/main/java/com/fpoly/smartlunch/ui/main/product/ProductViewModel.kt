package com.fpoly.smartlunch.ui.main.product

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.repository.AuthRepository
import com.fpoly.smartlunch.data.repository.ProductRepository
import com.fpoly.smartlunch.ui.main.home.HomeViewAction
import com.fpoly.smartlunch.ui.security.LoginViewEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ProductViewModel @AssistedInject constructor(
@Assisted state: ProductState,
private val repository: ProductRepository
): PolyBaseViewModel<ProductState, ProductAction, ProductEvent>(state){

    init {
        handleGetSize()
    }

    override fun handle(action: ProductAction) {
        when(action){
            is ProductAction.oneProduct -> handleGetProduct(action.id)
             is ProductAction.GetListSize -> handleGetSize()

            else -> {
            }
        }
    }


    private fun handleGetProduct(id: String?) {
        setState { copy(product = Loading()) }
        if(id != null){
            repository.getOneProducts(id)
                .execute {
                    copy(product = it)
                }
        }else{
          setState { copy(product = Fail(throw Throwable())) }
        }

    }
    private fun handleGetSize(){
        setState { copy(size = Loading()) }
        repository.getSize().execute {
            copy(size = it)
        }
    }
    private fun handleGetOne(id: String?) {
        setState { copy(oneSize = Loading()) }
        if(id != null){
            repository.getOneSize(id)
                .execute {
                    copy(oneSize = it)
                }
        }else{
            setState { copy(oneSize = Fail(throw Throwable())) }
        }

    }





    @AssistedFactory
    interface Factory {
        fun create(initialState: ProductState): ProductViewModel
    }

    companion object : MvRxViewModelFactory<ProductViewModel, ProductState> {
        override fun create(viewModelContext: ViewModelContext, state: ProductState): ProductViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }


}