package com.fpoly.smartlunch.ui.main.product

import android.util.Log
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.repository.ProductRepository
import com.fpoly.smartlunch.ui.main.home.HomeViewEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.http.Query

class ProductViewModel @AssistedInject constructor(
@Assisted state: ProductState,
private val repository: ProductRepository
): PolyBaseViewModel<ProductState, ProductAction, ProductEvent>(state){

    init {
        handleGetAllCategory()
        handleGetAllSize()
    }

    override fun handle(action: ProductAction) {
        when(action){
            is ProductAction.GetListProduct -> handleGetListProduct()
            is ProductAction.oneProduct -> handleGetOneProduct(action.id)
            is ProductAction.GetListSize -> handleGetAllSize()
            is ProductAction.oneSize -> handleGetSizeById(action.id)
            is ProductAction.CreateCart -> handleCreateCart(action.id,action.cart)
            is ProductAction.GetOneCartById -> handleGetOneCartById(action.id)
            is ProductAction.GetClearCart -> handleGetClearCartById(action.id)
            is ProductAction.GetChangeQuantity -> handleChangeQuantity(action.id, action.idProduct,action.changeQuantityRequest)
            is ProductAction.getRemoveProductByIdCart -> handleRemoveProductCart(action.id, action.idProduct, action.sizeId)
            is ProductAction.getAllProductByIdCategory -> handleAllProductByIdCategory(action.id)
            is ProductAction.incrementViewProduct -> handleGetViewProduct(action.id)

            else -> {
            }
        }
    }

    private fun handleGetListProduct() {
        setState { copy(products = Loading()) }
        repository.getProducts()
            .execute {
                copy(products = it)
            }
    }


    private fun handleGetOneProduct(id: String?) {
        setState { copy(product = Loading()) }
        if(id != null){
            repository.getOneProducts(id)
                .execute {
                    copy(product = it)
                }
        }

    }
    private fun handleGetAllSize(){
        setState { copy(size = Loading()) }
        repository.getSize().execute {
            copy(size = it)
        }
    }
    private fun handleGetSizeById(id: String?) {
        setState { copy(oneSize = Loading()) }
        if(id != null){
            repository.getOneSize(id)
                .execute {
                    copy(oneSize = it)
                }
        }

    }

    private fun handleCreateCart(id: String, cart: CartRequest){
        setState { copy(asyncCreateCart = Loading()) }
        if (id != null){
            repository.getCreateCart(id,cart)
                .execute {
                    copy(asyncCreateCart = it)
                }
        }
    }
    private fun handleGetOneCartById(id: String){
        setState { copy(getOneCartById = Loading()) }
        if(id != null){
            repository.getOneCartById(id)
                .execute {
                    copy(getOneCartById = it)
                }
        }else{
            setState { copy(getOneCartById = Fail(throw Throwable())) }
        }

    }
    private fun handleGetClearCartById(id: String){
        setState { copy(getClearCart = Loading()) }
            repository.getClearCart(id)
                .execute {
                    copy(getClearCart = it)
                }
    }
    private fun handleChangeQuantity(id: String ,idProduct: String, changeQuantityRequest: ChangeQuantityRequest){
        setState { copy(getChangeQuantity = Loading()) }
        repository.getChangeQuantityCart(id,idProduct,changeQuantityRequest)
            .execute {
                copy(getChangeQuantity = it)
            }
    }

    private fun handleRemoveProductCart(id: String, idProduct: String, sizeId: String){
        setState { copy(getRemoveProductByIdCart = Loading()) }
        repository.getRemoveGetOneProductCart(id, idProduct,sizeId)
            .execute {
                copy(getRemoveProductByIdCart = it)
            }
    }

    private fun handleGetAllCategory(){
        setState { copy(category = Loading()) }
        repository.getAllCategory().execute {
            copy(category = it)
        }
    }
    private fun handleAllProductByIdCategory(id : String){
        setState { copy(getAllProductByIdCategory = Loading()) }
        repository.getAllProductByIdCategory(id).execute {
            copy(getAllProductByIdCategory = it)
        }
    }
    private fun handleGetViewProduct(id : String){
        repository.getViewProduct(id).execute {
            copy()
        }
    }
   fun handleRemoveAsyncCreateCart(){
       setState { copy(asyncCreateCart = Uninitialized) }
   }

    fun handleRemoveAsyncClearCart(){
        setState { copy(getClearCart = Uninitialized) }
    }
    fun handleRemoveAsyncGetCart(){
        setState { copy(getOneCartById = Uninitialized) }
    }
    fun handleRemoveAsyncProductCart(){
        setState { copy(getRemoveProductByIdCart = Uninitialized) }
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