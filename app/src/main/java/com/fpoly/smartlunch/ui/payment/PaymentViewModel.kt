package com.fpoly.smartlunch.ui.payment

import android.annotation.SuppressLint
import android.os.Bundle
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.repository.PaymentRepository
import com.fpoly.smartlunch.data.repository.ProductRepository
import com.fpoly.smartlunch.data.repository.UserRepository
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductFragment
import com.fpoly.smartlunch.ui.main.search.SearchFragment
import com.fpoly.smartlunch.ui.payment.address.AddAddressFragment
import com.fpoly.smartlunch.ui.payment.address.AddressPaymentFragment
import com.fpoly.smartlunch.ui.payment.address.DetailAddressFragment
import com.fpoly.smartlunch.ui.payment.cart.CartFragment
import com.fpoly.smartlunch.ui.payment.cart.CouponsDetailFragment
import com.fpoly.smartlunch.ui.payment.cart.CouponsFragment
import com.fpoly.smartlunch.ui.payment.payment.PayFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException


@SuppressLint("CheckResult")
class PaymentViewModel @AssistedInject constructor(
    @Assisted state: PaymentViewState,
    private val productRepository: ProductRepository,
    private val paymentRepo: PaymentRepository,
) : PolyBaseViewModel<PaymentViewState, PaymentViewAction, PaymentViewEvent>(state) {

    init {
        handleGetListProduct()
        handleGetListCoupons()
        handleGetListTypePayment()
    }

    override fun handle(action: PaymentViewAction) {
        when (action) {
            is PaymentViewAction.getListPaymentType -> handleGetListTypePayment()
            is PaymentViewAction.setCurrentPaymentType -> setSelectItemPayment(action.menu)
            is PaymentViewAction.GetListProduct -> handleGetListProduct()
            is PaymentViewAction.GetDetailProduct -> handleGetOneProduct(action.id)
            is PaymentViewAction.IncrementViewProduct -> handleGetViewProduct(action.id)

            is PaymentViewAction.CreateOder -> handleCreateOrder(action.oder)
            is PaymentViewAction.UpdateOder -> handleUpdateOder(action.idOder, action.oder)

            is PaymentViewAction.GetListCoupons -> handleGetListCoupons()
            is PaymentViewAction.ApplyCoupon -> handleApplyCoupon(action.coupons)

            is PaymentViewAction.GetClearCart -> handleGetClearCartById()
            is PaymentViewAction.GetOneCartById -> handleGetOneCartById()
            is PaymentViewAction.GetChangeQuantity -> handleChangeQuantity(
                action.idProduct,
                action.changeQuantityRequest
            )
            is PaymentViewAction.GetRemoveProductByIdCart -> handleRemoveProductCart(
                action.idProduct,
                action.sizeId
            )

            is PaymentViewAction.GetProvinceAddress -> handleGetListProvince()
            is PaymentViewAction.GetDistrictAddress -> handleGetListDistrict(action.provinceId)
            is PaymentViewAction.GetWardAddress -> handleGetListWard(action.districtId)
            else -> {}
        }
    }

    private fun handleGetClearCartById() {
        setState { copy(asyncGetClearCart = Loading()) }
        productRepository.getClearCart()
            .execute {
                copy(asyncGetClearCart = it)
            }
    }

    private fun handleApplyCoupon(coupons: CouponsRequest) {
        setState { copy(asyncCurentCart = Loading()) }
        productRepository.applyCoupon(coupons)
            .subscribe(
                { response ->
                    setState { copy(asyncCurentCart = Success(response)) }
                }, { throwable ->
                    if (throwable is HttpException) {
                        setState {
                            copy(
                                catchError = when (throwable.code()) {
                                    400 -> {
                                        "Không thể sử dụng mã"
                                    }

                                    else -> {
                                        ""
                                    }
                                }, asyncCurentCart = Fail(throwable)
                            )
                        }
                    }
                })
    }

    private fun handleGetListCoupons() {
        setState { copy(asyncCoupons = Loading()) }
        productRepository.getCoupons()
            .execute {
                copy(asyncCoupons = it)
            }
    }

    private fun handleGetListTypePayment() {
        paymentRepo.getTypePayment().subscribe() {
            setState {
                copy(paymentTypies = it, curentPaymentType = it[0])
            }
        }
    }

    private fun setSelectItemPayment(menu: Menu) {
        setState {
            copy(curentPaymentType = menu)
        }
    }

    private fun handleCreateOrder(oder: OrderRequest) {
        setState { copy(asyncAddOrder = Loading()) }
        productRepository.createOrder(oder)
            .execute {
                copy(asyncAddOrder = it)
            }
    }

    private fun handleUpdateOder(idOrder: String, oder: OrderRequest) {
        setState { copy(asyncUpdateOrder = Loading()) }
        productRepository.updateOrder(idOrder, oder)
            .execute {
                copy(asyncUpdateOrder = it)
            }
    }

    private fun handleGetListProduct() {
        setState { copy(asyncProducts = Loading()) }
        productRepository.getProducts(null)
            .execute {
                copy(asyncProducts = it)
            }
    }


    private fun handleGetOneProduct(id: String?) {
        setState { copy(asyncProduct = Loading()) }
        if (id != null) {
            productRepository.getOneProducts(id)
                .execute {
                    copy(asyncProduct = it)
                }
        }

    }

    private fun handleGetOneCartById() {
        setState { copy(asyncCurentCart = Loading()) }
        productRepository.getOneCartById()
            .execute {
                copy(asyncCurentCart = it)
            }
    }

    private fun handleChangeQuantity(
        idProduct: String,
        changeQuantityRequest: ChangeQuantityRequest
    ) {
        setState { copy(asyncCurentCart = Loading()) }
        productRepository.getChangeQuantityCart(idProduct, changeQuantityRequest)
            .execute {
                copy(asyncCurentCart = it)
            }

    }

    private fun handleRemoveProductCart(idProduct: String, sizeId: String) {
//        setState { copy(getRemoveProductByIdCart = Loading()) }
//        repository.getRemoveGetOneProductCart(idProduct, sizeId)
//            .execute {
//                copy(getRemoveProductByIdCart = it)
//            }
        setState { copy(asyncCurentCart = Loading()) }
        productRepository.getRemoveGetOneProductCart(idProduct, sizeId)
            .execute {
                copy(asyncCurentCart = it)
            }
    }

    private fun handleGetViewProduct(id: String) {
        productRepository.getViewProduct(id).execute {
            copy()
        }
    }

    private fun handleGetListProvince() {
        setState {
            copy(
                asyncListProvince = Loading(),
                asyncListDistrict = Uninitialized,
                asyncListWard = Uninitialized
            )
        }
        paymentRepo.getProvince().execute {
            copy(asyncListProvince = it)
        }
    }

    private fun handleGetListDistrict(provinceId: String) {
        setState { copy(asyncListDistrict = Loading(), asyncListWard = Uninitialized) }
        paymentRepo.getDistrict(provinceId).execute {
            copy(asyncListDistrict = it)
        }
    }

    private fun handleGetListWard(districtId: String) {
        setState { copy(asyncListWard = Loading()) }
        paymentRepo.getWard(districtId).execute {
            copy(asyncListWard = it)
        }
    }


    fun handleRemoveAsyncGetCart() {
        setState { copy(asyncCurentCart = Uninitialized) }
    }

    fun handleRemoveAsyncChangeQuantity() {
        setState { copy(asyncCurentCart = Uninitialized) }
    }

    fun returnHomeFragment() {
        _viewEvents.post(PaymentViewEvent.ReturnFragment(CartFragment::class.java))
    }

    fun returnDetailProductFragment() {
        _viewEvents.post(PaymentViewEvent.ReturnFragment(ProductFragment::class.java))
    }

    fun returnSearchProductFragment(bundle: Bundle? = null) {
        _viewEvents.post(PaymentViewEvent.ReturnFragmentWithArgument(SearchFragment::class.java, bundle))
    }

    fun returnAddressFragment() {
        _viewEvents.post(PaymentViewEvent.ReturnFragment(AddressPaymentFragment::class.java))
    }

    fun returnAddAddressFragment() {
        _viewEvents.post(PaymentViewEvent.ReturnFragment(AddAddressFragment::class.java))
    }

    fun returnDetailAddressFragment() {
        _viewEvents.post(PaymentViewEvent.ReturnFragment(DetailAddressFragment::class.java))
    }

    fun returnShowLoading(isVisible: Boolean) {
        _viewEvents.post(PaymentViewEvent.ReturnShowLoading(isVisible))
    }

    fun returnPayFragment(bundle: Bundle) {
        _viewEvents.post(
            PaymentViewEvent.ReturnFragmentWithArgument(
                PayFragment::class.java,
                bundle
            )
        )
    }

    fun returnCouponsFragment() {
        _viewEvents.post(PaymentViewEvent.ReturnFragment(CouponsFragment::class.java))
    }

    fun returnCouponsDetailFragment() {
        _viewEvents.post(PaymentViewEvent.ReturnFragment(CouponsDetailFragment::class.java))
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: PaymentViewState): PaymentViewModel
    }

    companion object : MvRxViewModelFactory<PaymentViewModel, PaymentViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: PaymentViewState
        ): PaymentViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }


}