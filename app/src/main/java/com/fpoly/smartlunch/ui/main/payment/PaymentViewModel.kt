package com.fpoly.smartlunch.ui.main.payment

import android.os.Bundle
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.repository.ProductRepository
import com.fpoly.smartlunch.ui.main.product.ProductFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class PaymentViewModel @AssistedInject constructor(
    @Assisted state: PaymentViewState,
    private val repository: ProductRepository,
): PolyBaseViewModel<PaymentViewState, PaymentViewAction, PaymentViewEvent>(state){

    init {
        handleGetListProduct()
        handleGetListCoupons()
    }

    override fun handle(action: PaymentViewAction) {
        when(action){
            is PaymentViewAction.GetClearCart -> handleGetClearCartById(action.id)
            is PaymentViewAction.GetListProduct -> handleGetListProduct()
            is PaymentViewAction.GetDetailProduct -> handleGetOneProduct(action.id)
            is PaymentViewAction.GetOneCartById -> handleGetOneCartById(action.id)
            is PaymentViewAction.GetChangeQuantity -> handleChangeQuantity(action.id, action.idProduct,action.changeQuantityRequest)
            is PaymentViewAction.IncrementViewProduct -> handleGetViewProduct(action.id)
            is PaymentViewAction.CreateOder -> handleCreateOrder(action.oder)
            is PaymentViewAction.GetListCoupons -> handleGetListCoupons()
            is PaymentViewAction.ApplyCoupon -> handleApplyCoupon(action.id,action.coupons)
        }
    }

    private fun handleGetClearCartById(id: String) {
        setState { copy(asyncGetClearCart = Loading()) }
        repository.getClearCart(id)
            .execute {
                copy(asyncGetClearCart = it)
            }
    }

    private fun handleApplyCoupon(id: String, coupons: CouponsRequest) {
        setState { copy(asyncApplyCoupons = Loading()) }
        repository.applyCoupon(id,coupons)
            .execute {
                copy(asyncApplyCoupons=it)
            }
    }

    private fun handleGetListCoupons() {
        setState { copy(asyncCoupons = Loading()) }
        repository.getCoupons()
            .execute {
                copy(asyncCoupons = it)
            }
    }

    private fun handleCreateOrder(oder: OrderRequest) {
        setState { copy(asyncAddOrder = Loading()) }
        repository.createOrder(oder)
            .execute {
                copy(asyncAddOrder = it)
            }
    }


    private fun handleGetListProduct() {
        setState { copy(asyncProducts = Loading()) }
        repository.getProducts()
            .execute {
                copy(asyncProducts = it)
            }
    }


    private fun handleGetOneProduct(id: String?) {
        setState { copy(asyncProduct = Loading()) }
        if(id != null){
            repository.getOneProducts(id)
                .execute {
                    copy(asyncProduct = it)
                }
        }

    }

    private fun handleGetOneCartById(id: String){
        setState { copy(asyncGetOneCartById = Loading()) }
        if(id != null){
            repository.getOneCartById(id)
                .execute {
                    copy(asyncGetOneCartById = it)
                }
        }
    }

    private fun handleChangeQuantity(id: String ,idProduct: String, changeQuantityRequest: ChangeQuantityRequest){
        setState { copy(asyncGetChangeQuantity = Loading()) }
        repository.getChangeQuantityCart(id,idProduct,changeQuantityRequest)
            .execute {
                copy(asyncGetChangeQuantity = it)
            }
    }


    private fun handleGetViewProduct(id : String){
        repository.getViewProduct(id).execute {
            copy()
        }
    }

    fun handleRemoveAsyncGetCart(){
        setState { copy(asyncGetOneCartById = Uninitialized) }
    }
    fun handleRemoveAsyncChangeQuantity(){
        setState { copy(asyncGetChangeQuantity = Uninitialized) }
    }

    fun returnHomeFragment(){
        _viewEvents.post(PaymentViewEvent.ReturnFragment(CartFragment::class.java))
    }

    fun returnDetailProductFragment(){
        _viewEvents.post(PaymentViewEvent.ReturnFragment(ProductFragment::class.java))
    }
    fun returnAddressFragment(){
        _viewEvents.post(PaymentViewEvent.ReturnFragment(AddressPaymentFragment::class.java))
    }
    fun returnPayFragment(bundle: Bundle){
        _viewEvents.post(PaymentViewEvent.ReturnFragmentWithArgument(PayFragment::class.java,bundle))
    }


//    fun handleUpdateCart(){
//        _viewEvents.post(ProductEvent.UpdateCart)
//    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: PaymentViewState): PaymentViewModel
    }

    companion object : MvRxViewModelFactory<PaymentViewModel, PaymentViewState> {
        override fun create(viewModelContext: ViewModelContext, state: PaymentViewState): PaymentViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }




}