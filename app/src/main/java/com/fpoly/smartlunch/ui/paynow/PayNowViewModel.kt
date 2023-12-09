package com.fpoly.smartlunch.ui.paynow

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.CartLocalRequest
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.ToppingCart
import com.fpoly.smartlunch.data.repository.PaymentRepository
import com.fpoly.smartlunch.data.repository.ProductRepository
import com.fpoly.smartlunch.ui.main.product.ProductFragment
import com.fpoly.smartlunch.ui.main.search.SearchFragment
import com.fpoly.smartlunch.ui.payment.PaymentViewEvent
import com.fpoly.smartlunch.ui.payment.address.AddAddressFragment
import com.fpoly.smartlunch.ui.payment.address.AddressPaymentFragment
import com.fpoly.smartlunch.ui.payment.address.DetailAddressFragment
import com.fpoly.smartlunch.ui.payment.cart.CartFragment
import com.fpoly.smartlunch.ui.payment.cart.CouponsDetailFragment
import com.fpoly.smartlunch.ui.payment.cart.CouponsFragment
import com.fpoly.smartlunch.ui.payment.payment.PayFragment
import com.fpoly.smartlunch.ui.paynow.payment.PaymentNowFragment
import com.fpoly.smartlunch.ultis.StringUltis.dateIso8601Format
import com.fpoly.smartlunch.ultis.StringUltis.dateIso8601Format2
import com.fpoly.smartlunch.ultis.convertToDateFormat
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException


@SuppressLint("CheckResult")
class PayNowViewModel @AssistedInject constructor(
    @Assisted state: PayNowViewState,
    private val productRepository: ProductRepository,
    private val paymentRepo: PaymentRepository,
) : PolyBaseViewModel<PayNowViewState, PayNowViewAction, PayNowViewEvent>(state) {

    init {
    }

    override fun handle(action: PayNowViewAction) {
        when (action) {
            is PayNowViewAction.SetCurrentCart -> handleSetCurrentCartLocal(action.cartResponse)
            is PayNowViewAction.SetCupontCart -> handleSetCoupondCartLocal(action.currentCart, action.coupons)
            is PayNowViewAction.ChangeQuantityProduct -> handleChangeQuantityProduct(action.currentCart, action.idProduct, action.changeQuantityRequest)
            is PayNowViewAction.CheckUpdateCartLocal -> handleCheckUpdateCartLocal(action.currentCart)
            is PayNowViewAction.CreateOder -> handleCreateOrder(action.currentCart, action.orderRequest)
            else -> {}
        }
    }

    fun handleSetCurrentCartLocal(cartResponse: CartResponse) {
        setState {
            copy(curentCart = Success(cartResponse))
        }
    }

    private fun handleSetCoupondCartLocal(currentCart: CartResponse?, coupons: CouponsResponse) {
        if (currentCart == null){
            return
        }
        setState {
            copy( curentCart = Loading())
        }
        productRepository.applyCouponLocal(CartLocalRequest(currentCart, CouponsRequest(coupons._id)))
            .execute {
                copy(curentCart = it)
            }
    }
    private fun handleChangeQuantityProduct(currentCart: CartResponse?, idProduct: String, changeQuantityRequest: ChangeQuantityRequest) {
        if (currentCart == null){
            return
        }
        setState {
            copy( curentCart = Loading())
        }
        productRepository.getChangeQuantityCartLocal(idProduct, CartLocalRequest(currentCart, changeQuantityRequest))
            .execute {
                copy(curentCart = it)
        }
    }

    private fun handleCheckUpdateCartLocal(currentCart: CartResponse?) {
        if (currentCart == null){
            return
        }
        setState {
            copy( curentCart = Loading())
        }
        productRepository.getCheckUpdateCartLocal(CartLocalRequest(currentCart, null))
            .execute {
                copy(curentCart = it)
            }
    }

    fun returnPayFragment(bundle: Bundle) {
        _viewEvents.post(
            PayNowViewEvent.ReturnFragmentWithArgument(
                PaymentNowFragment::class.java,
                bundle
            )
        )
    }

    private fun handleTotalCart(currentCart: CartResponse): Double{
        if (currentCart.products.isEmpty()) return 0.0
        var total = 0.0
        currentCart.products.forEach{productCart ->
            total += productCart.sizeId.size_price * productCart.purchase_quantity
            productCart.toppings.forEach{toppingCart ->
                total += toppingCart._id.price * toppingCart._quantity
            }
        }
        return total
    }

    private fun handleCreateOrder(currentCart: CartResponse?, orderRequest: OrderRequest) {
        if (currentCart == null){
            return
        }
        setState { copy(asyncAddOrder = Loading()) }
        productRepository.createOrderCartLocal(CartLocalRequest(currentCart, orderRequest))
            .execute {
                copy(asyncAddOrder = it)
            }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: PayNowViewState): PayNowViewModel
    }

    companion object : MvRxViewModelFactory<PayNowViewModel, PayNowViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: PayNowViewState
        ): PayNowViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }


}