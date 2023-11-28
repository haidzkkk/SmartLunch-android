package com.fpoly.smartlunch.ui.main.product

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CommentRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.PagingRequestProduct
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.SortPagingProduct
import com.fpoly.smartlunch.data.repository.NotificationRepository
import com.fpoly.smartlunch.data.repository.ProductRepository
import com.fpoly.smartlunch.ui.main.comment.CommentFragment
import com.fpoly.smartlunch.ui.main.home.HomeViewEvent
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ultis.Status.CONFIRMED_STATUS
import com.fpoly.smartlunch.ultis.Status.DELIVERING_STATUS
import com.fpoly.smartlunch.ultis.Status.UNCONFIRMED_STATUS
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException

class ProductViewModel @AssistedInject constructor(
    @Assisted state: ProductState,
    private val repository: ProductRepository,
    private val notificationRepository: NotificationRepository
) : PolyBaseViewModel<ProductState, ProductAction, ProductEvent>(state) {

    init {

//        handleGetAllCategory()
//        handleGetAllSize()
        handleGetAllCategory()
        handleGetAllFavouriteProduct()
        handleGetTopProduct(PagingRequestProduct(5, SortPagingProduct.bought, null, null, null))
        handleGetListProductRate(PagingRequestProduct(5, SortPagingProduct.rate, null, null, null))
        handleGetProducts(PagingRequestProduct(2, null, null, 1, null))
        handleGetAllOrderByUserId()
        handleGetAllNotification()
        handleGetListCoupons()
    }

    override fun handle(action: ProductAction) {
        when (action) {
            is ProductAction.GetListProductRate -> handleGetListProductRate(action.paging)
            is ProductAction.GetListProduct -> handleGetProducts(action.paging)
            is ProductAction.GetListTopProduct -> handleGetTopProduct(action.paging)
            is ProductAction.GetAllCategory -> handleGetAllCategory()
            is ProductAction.GetAllFavouriteProduct -> handleGetAllFavouriteProduct()
            is ProductAction.GetDetailProduct -> handleGetOneProduct(action.id)
            is ProductAction.GetDetailCoupons -> handleGetDetailCoupons(action.id)
            is ProductAction.GetListSizeProduct -> handleGetSizeProduct(action.idProduct)
            is ProductAction.GetSizeById -> handleGetSizeById(action.id)

            is ProductAction.CreateCart -> handleCreateCart(action.cart)
            is ProductAction.GetOneCartById -> handleGetOneCartById()
            is ProductAction.GetClearCart -> handleGetClearCartById()
            is ProductAction.GetChangeQuantity -> handleChangeQuantity(
                action.idProduct,
                action.changeQuantityRequest
            )

            is ProductAction.GetRemoveProductByIdCart -> handleRemoveProductCart(
                action.idProduct,
                action.sizeId
            )


            is ProductAction.UpdateOder -> handleUpdateOder(action.idOder, action.oder)
            is ProductAction.GetAllProductByIdCategory -> handleAllProductByIdCategory(action.id)
            is ProductAction.GetAllOrderByUserId -> handleGetAllOrderByUserId()
            is ProductAction.GetCurrentOrder -> handleGetCurrentOrder(action.id)
            is ProductAction.LikeProduct -> handleLikeProduct(action.product)

            is ProductAction.GetListComments -> handleGetListComments(action.productId)
            is ProductAction.GetListCommentsLimit -> handleGetListCommentsLimit(action.productId)
            is ProductAction.AddComment -> handleAddComments(action.comment, action.images)

            is ProductAction.GetAllNotification -> handleGetAllNotification()
            is ProductAction.GetReadNotification -> handleReadNotification(action.id)
            is ProductAction.ApplyCoupon -> handleApplyCoupon(action.coupons)
            is ProductAction.SearchProductByName -> handleSearchProductByName(action.text)
            else -> {}
        }
    }

    private fun handleSearchProductByName(text: String) {
        setState { copy(currentProductsSearch = Loading()) }
        repository.searchProductByName(text).execute {
            copy(currentProductsSearch = it)
        }
    }

    private fun handleApplyCoupon(coupons: CouponsRequest) {
        setState { copy(asyncCurentCart = Loading()) }
        repository.applyCoupon(coupons)
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

    private fun handleGetDetailCoupons(id: String) {
        setState { copy(asyncOneCoupons = Loading()) }
        repository.getOneCoupons(id)
            .execute {
                copy(asyncOneCoupons = it)
            }
    }

    private fun handleGetListCoupons() {
        setState { copy(asyncCoupons = Loading()) }
        repository.getCoupons()
            .execute {
                copy(asyncCoupons = it)
            }
    }

    private fun handleReadNotification(id: String) {
        setState { copy(asyncReadNotification = Loading()) }
        notificationRepository.getReadNotification(id)
            .execute {
                copy(asyncReadNotification = it)
            }
    }

    private fun handleGetAllNotification() {
        setState { copy(asyncNotifications = Loading(), asyncUnreadNotifications = Loading()) }
        notificationRepository.getAllNotification()
            .execute {
                copy(asyncNotifications = it)
            }
        notificationRepository.getAllNotificationWithQuery(false)
            .execute {
                copy(asyncUnreadNotifications = it)
            }
    }

    private fun handleGetProducts(paging: PagingRequestProduct) {
        setState { copy(asyncProducts = Loading()) }
        repository.getProducts(paging)
            .execute {
                copy(asyncProducts = it)
            }
    }

    private fun handleGetTopProduct(paging: PagingRequestProduct) {
        setState { copy(asyncTopProduct = Loading()) }
        repository.getProducts(paging)
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

    private fun handleGetAllOrderByUserId() {
        setState {
            copy(
                asyncOrders = Loading(),
                asyncUnconfirmed = Loading(),
                asyncConfirmed = Loading(),
                asyncDelivering = Loading()
            )
        }
        repository.getAllOrderByUserId("")
            .execute {
                copy(asyncOrders = it)
            }
        repository.getAllOrderByUserId(UNCONFIRMED_STATUS)
            .execute {
                copy(asyncUnconfirmed = it)
            }
        repository.getAllOrderByUserId(CONFIRMED_STATUS)
            .execute {
                copy(asyncConfirmed = it)
            }
        repository.getAllOrderByUserId(DELIVERING_STATUS)
            .execute {
                copy(asyncDelivering = it)
            }
    }

    private fun handleGetListProductRate(pagingRequestProduct: PagingRequestProduct) {
        setState { copy(productsRate = Loading()) }
        repository.getProducts(pagingRequestProduct)
            .execute {
                copy(productsRate = it)
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


    private fun handleGetListComments(idProduct: String) {
        setState { copy(asyncComments = Loading()) }
        repository.getCommentProduct(idProduct)
            .execute {
                copy(asyncComments = it)
            }
    }
    private fun handleGetListCommentsLimit(idProduct: String) {
        setState { copy(asyncCommentsLimit = Loading()) }
        repository.getCommentProductLimit(idProduct, 2)
            .execute {
                copy(asyncCommentsLimit = it)
            }
    }

    private fun handleAddComments(comment: CommentRequest, images: List<Gallery>?) {
        setState { copy(asyncAddComment = Loading()) }
        repository.postMessage(comment, images)
            .execute {
                copy(asyncAddComment = it)
            }
    }

    private fun handleGetSizeProduct(idProduct: String) {
        setState { copy(asynGetSizeProduct = Loading()) }
        repository.getSizeProduct(idProduct).execute {
            copy(asynGetSizeProduct = it)
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

    private fun handleCreateCart(cart: CartRequest) {
        setState { copy(asyncCreateCart = Loading()) }
        repository.getCreateCart(cart)
            .execute {
                copy(asyncCreateCart = it)
            }
    }

    private fun handleGetOneCartById() {
//        setState { copy(getOneCartById = Loading()) }
//        repository.getOneCartById()
//            .execute {
//                copy(getOneCartById = it)
//            }
        setState { copy(curentCartResponse = Loading()) }
        repository.getOneCartById()
            .execute {
                copy(curentCartResponse = it)
            }
    }

    private fun handleGetClearCartById() {
//        setState { copy(getClearCart = Loading()) }
//        repository.getClearCart()
//            .execute {
//                copy(getClearCart = it)
//            }
        setState { copy(curentCartResponse = Loading()) }
        repository.getClearCart()
            .execute {
                copy(curentCartResponse = it)
            }
    }

    private fun handleChangeQuantity(
        idProduct: String,
        changeQuantityRequest: ChangeQuantityRequest
    ) {
//        setState { copy(getChangeQuantity = Loading()) }
//        repository.getChangeQuantityCart(idProduct, changeQuantityRequest)
//            .execute {
//                copy(getChangeQuantity = it)
//            }
        setState { copy(curentCartResponse = Loading()) }
        repository.getChangeQuantityCart(idProduct, changeQuantityRequest)
            .execute {
                copy(curentCartResponse = it)
            }
    }

    private fun handleRemoveProductCart(idProduct: String, sizeId: String) {
//        setState { copy(getRemoveProductByIdCart = Loading()) }
//        repository.getRemoveGetOneProductCart(idProduct, sizeId)
//            .execute {
//                copy(getRemoveProductByIdCart = it)
//            }
        setState { copy(curentCartResponse = Loading()) }
            repository.getRemoveGetOneProductCart(idProduct, sizeId)
                .execute {
                    copy(curentCartResponse = it)
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

    private fun handleUpdateOder(idOrder: String, oder: OrderRequest) {
        setState { copy(asyncUpdateOrder = Loading()) }
        repository.updateOrder(idOrder, oder)
            .execute {
                copy(asyncUpdateOrder = it)
            }
    }


    fun returnVisibleBottomNav(isVisible: Boolean) {
        _viewEvents.post(ProductEvent.ReturnVisibleBottomNav(isVisible))
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