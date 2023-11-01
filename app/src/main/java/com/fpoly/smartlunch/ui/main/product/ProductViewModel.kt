package com.fpoly.smartlunch.ui.main.product

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.repository.ProductRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ProductViewModel @AssistedInject constructor(
    @Assisted state: ProductState,
    private val repository: ProductRepository
) : PolyBaseViewModel<ProductState, ProductAction, ProductEvent>(state) {

    init {
        handleGetAllCategory()
        handleGetListProduct()
        handleGetAllSize()
        handleGetAllFavouriteProduct()
        handleGetTopProduct()
    }

    override fun handle(action: ProductAction) {
        when (action) {
            is ProductAction.GetListProduct -> handleGetListProduct()
            is ProductAction.GetDetailProduct -> handleGetOneProduct(action.id)
            is ProductAction.GetListSize -> handleGetAllSize()
            is ProductAction.GetSizeById -> handleGetSizeById(action.id)
            is ProductAction.CreateCart -> handleCreateCart(action.id, action.cart)
            is ProductAction.GetOneCartById -> handleGetOneCartById(action.id)
            is ProductAction.GetClearCart -> handleGetClearCartById(action.id)
            is ProductAction.GetChangeQuantity -> handleChangeQuantity(
                action.id,
                action.idProduct,
                action.changeQuantityRequest
            )

            is ProductAction.GetRemoveProductByIdCart -> handleRemoveProductCart(
                action.id,
                action.idProduct,
                action.sizeId
            )

            is ProductAction.GetAllProductByIdCategory -> handleAllProductByIdCategory(action.id)
            is ProductAction.GetAllOrderByUserId -> handleGetAllOrderByUserId(action.userId)
            is ProductAction.GetCurrentOrder -> handleGetCurrentOrder(action.id)
            is ProductAction.LikeProduct -> handleLikeProduct(action.product)
        }
    }

    private fun handleGetTopProduct() {
        setState { copy(asyncTopProduct = Loading()) }
        repository.getTopViewedProducts()
            .execute {
                copy(asyncTopProduct = it)
            }
    }

    private fun handleGetAllFavouriteProduct() {
        setState { copy(asyncFavourites = Loading()) }
        repository.getAllFavourite()
            .execute {
                copy(asyncFavourites = it)
            }
    }

    private fun handleLikeProduct(product: Product) {
        setState { copy(asyncLikeProduct = Loading()) }
        repository.likeProduct(product)
            .execute {
                copy(asyncLikeProduct = it)
            }
    }

    private fun handleGetCurrentOrder(id: String) {
        setState { copy(addOrder = Loading()) }
        repository.getCurrentOrder(id)
            .execute {
                copy(addOrder = it)
            }
    }

    private fun handleGetAllOrderByUserId(userId: String) {
        setState { copy(asyncOrders = Loading(), asyncOrderings = Loading()) }
        repository.getAllOrderByUserId(userId, "")
            .execute {
                copy(asyncOrders = it)
            }
        repository.getAllOrderByUserId(userId, "65264bc32d9b3bb388078974")
            .execute {
                copy(asyncOrderings = it)
            }
    }

    private fun handleGetListProduct() {
        setState { copy(products = Loading()) }
        repository.getProducts()
            .execute {
                copy(products = it)
            }
    }


    private fun handleGetOneProduct(id: String) {
        setState { copy(asyncProduct = Loading(), asyncGetFavourite = Loading()) }
        repository.getOneProducts(id)
            .execute {
                copy(asyncProduct = it)
            }
        repository.getFavourite(id)
            .execute {
                copy(asyncGetFavourite = it)
            }
        repository.getViewProduct(id).execute {
            copy()
        }
    }

    private fun handleGetAllSize() {
        setState { copy(asynGetAllSize = Loading()) }
        repository.getSize().execute {
            copy(asynGetAllSize = it)
        }
    }

    private fun handleGetSizeById(id: String?) {
        setState { copy(asyncGetOneSize = Loading()) }
        if (id != null) {
            repository.getOneSize(id)
                .execute {
                    copy(asyncGetOneSize = it)
                }
        }

    }

    private fun handleCreateCart(id: String, cart: CartRequest) {
        setState { copy(asyncCreateCart = Loading()) }
        if (id != null) {
            repository.getCreateCart(id, cart)
                .execute {
                    copy(asyncCreateCart = it)
                }
        }
    }

    private fun handleGetOneCartById(id: String) {
        setState { copy(getOneCartById = Loading()) }
        if (id != null) {
            repository.getOneCartById(id)
                .execute {
                    copy(getOneCartById = it)
                }
        }
    }

    private fun handleGetClearCartById(id: String) {
        setState { copy(getClearCart = Loading()) }
        repository.getClearCart(id)
            .execute {
                copy(getClearCart = it)
            }
    }

    private fun handleChangeQuantity(
        id: String,
        idProduct: String,
        changeQuantityRequest: ChangeQuantityRequest
    ) {
        setState { copy(getChangeQuantity = Loading()) }
        repository.getChangeQuantityCart(id, idProduct, changeQuantityRequest)
            .execute {
                copy(getChangeQuantity = it)
            }
    }

    private fun handleRemoveProductCart(id: String, idProduct: String, sizeId: String) {
        setState { copy(getRemoveProductByIdCart = Loading()) }
        repository.getRemoveGetOneProductCart(id, idProduct, sizeId)
            .execute {
                copy(getRemoveProductByIdCart = it)
            }
    }

    private fun handleGetAllCategory() {
        setState { copy(category = Loading()) }
        repository.getAllCategory().execute {
            copy(category = it)
        }
    }

    private fun handleAllProductByIdCategory(id: String) {
        setState { copy(getAllProductByIdCategory = Loading()) }
        repository.getAllProductByIdCategory(id).execute {
            copy(getAllProductByIdCategory = it)
        }
    }

    fun handleRemoveAsyncClearCart() {
        setState { copy(getClearCart = Uninitialized) }
    }

    fun handleRemoveAsyncProductCart() {
        setState { copy(getRemoveProductByIdCart = Uninitialized) }
    }

    fun handleRemoveAsyncOneSize() {
        setState { copy(asyncGetOneSize = Uninitialized) }
    }

    fun handleRemoveAsyncChangeQuantity() {
        setState { copy(getChangeQuantity = Uninitialized) }
    }
    fun handleRemoveAsyncGetFavourite() {
        setState { copy(asyncGetFavourite = Uninitialized) }
    }

    fun handleUpdateCart() {
        _viewEvents.post(ProductEvent.UpdateCart)
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: ProductState): ProductViewModel
    }

    companion object : MvRxViewModelFactory<ProductViewModel, ProductState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: ProductState
        ): ProductViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }


}