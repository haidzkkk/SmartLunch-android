package com.fpoly.smartlunch.ui.main.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.databinding.FragmentOrderDetailBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ultis.Status
import com.fpoly.smartlunch.ultis.StringUltis
import com.fpoly.smartlunch.ultis.convertIsoToStringFormat
import com.fpoly.smartlunch.ultis.formatCash
import com.fpoly.smartlunch.ultis.setTextColor
import com.fpoly.smartlunch.ultis.showSnackbar

class OrderDetailFragment : PolyBaseFragment<FragmentOrderDetailBinding>() {
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private var currentOrder: OrderResponse? = null

    private lateinit var productOrderAdapter: ProductOrderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        listenEvent()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        withState(productViewModel){
            it.addOrder = Uninitialized
            it.asyncUpdateOrder = Uninitialized
        }
    }
    private fun initUI() {
        productOrderAdapter = ProductOrderAdapter {
            productViewModel.handle(ProductAction.GetDetailProduct(it.productId))
            productViewModel.handle(ProductAction.GetListSizeProduct(it.productId))
            productViewModel.handle(ProductAction.GetListCommentsLimit(it.productId))
            homeViewModel.returnDetailProductFragment()
        }
        views.rcvProduct.adapter = productOrderAdapter
    }

    private fun listenEvent() {
        views.swipeLoading.setOnRefreshListener {
            if (currentOrder != null){
                productViewModel.handle(ProductAction.GetCurrentOrder(currentOrder!!._id))
            }
        }
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
        views.followOrderText.setOnClickListener {
            homeViewModel.returnTrackingOrderFragment()
        }
        views.btnConfirmReceived.setOnClickListener{
            homeViewModel.returnProductReviewFragment()
        }
        views.btnCancel.setOnClickListener{
            if (currentOrder != null){
                var orderRequest = OrderRequest(null, null, Status.CANCEL_STATUS, null, null)
                productViewModel.handle(ProductAction.UpdateOder(currentOrder!!._id, orderRequest))
            }
        }
    }

    private fun setupUI(currentOrder: OrderResponse) {
        productOrderAdapter.setData(currentOrder)

        views.appBar.apply {
            btnBackToolbar.visibility = View.VISIBLE
            tvTitleToolbar.text = getString(R.string.order)
        }
        views.apply {
            recipientName.text = currentOrder.address.recipientName
            orderTimeValue.text = currentOrder.createdAt.convertIsoToStringFormat(StringUltis.dateDay2TimeFormat)
            pickupTimeValue.text = currentOrder.updatedAt.convertIsoToStringFormat(StringUltis.dateDay2TimeFormat)
            orderCodeValue.text = currentOrder._id

            tvTitleStatus.text = currentOrder.status.status_name
            tvMessageStatus.text = currentOrder.status.status_description

            location.text = currentOrder.address.addressLine
            phoneNumber.text = currentOrder.address.phoneNumber

            layoutCupond.isVisible = currentOrder.couponId != null
            if (currentOrder.couponId  != null){
                tvIdCupond.text = currentOrder.couponId
            }

            tvPrice.text = currentOrder.discount.formatCash()
            tvTypePaymentName.text = currentOrder.statusPayment.status_name
            tvIsPayment.text = if (currentOrder.isPayment) "Đã thanh toán" else "Chưa thanh toán"

            followOrderText.isVisible = currentOrder.status._id == Status.DELIVERING_STATUS
            btnConfirmReceived.isEnabled = currentOrder.status._id == Status.SUCCESS_STATUS
            btnCancel.isEnabled = currentOrder.status._id == Status.UNCONFIRMED_STATUS
            tvTitleStatus.setTextColor(currentOrder.status._id == Status.SUCCESS_STATUS)
        }

        handleStateProgress(currentOrder)
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        homeViewModel.returnVisibleBottomNav(true)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOrderDetailBinding {
        return FragmentOrderDetailBinding.inflate(inflater, container, false)
    }

    override fun invalidate():Unit= withState(productViewModel) {
        views.swipeLoading.isRefreshing = it.addOrder is Loading || it.asyncUpdateOrder is Loading

        when(it.addOrder){
            is Success -> {
                currentOrder= it.addOrder.invoke()
                currentOrder?.let { currentOrder -> setupUI(currentOrder) }
            }
            is Fail -> {
            }
            else -> {}
        }
        when(it.asyncUpdateOrder){
            is Success -> {
                showSnackbar(views.root, "Hủy đơn hàng thành công", true, null, null)
                currentOrder= it.asyncUpdateOrder.invoke()
                currentOrder?.let { currentOrder -> setupUI(currentOrder) }
                it.asyncUpdateOrder = Uninitialized
            }
            is Fail -> {
                showSnackbar(views.root, "Hủy đơn hàng thất bại", true, "thử lại"){
                    if (currentOrder != null){
                        var orderRequest = OrderRequest(null, null, Status.CANCEL_STATUS, null, null)
                        productViewModel.handle(ProductAction.UpdateOder(currentOrder!!._id, orderRequest))
                    }
                }
                it.asyncUpdateOrder = Uninitialized
            }
            else -> {}
        }
    }

    fun handleStateProgress(currentOrder: OrderResponse){
        var index = when(currentOrder.status._id){
            Status.UNCONFIRMED_STATUS -> 0
            Status.CONFIRMED_STATUS -> 1
            Status.DELIVERING_STATUS -> 2
            Status.SUCCESS_STATUS -> 3
            Status.CANCEL_STATUS -> 4
            else -> 0
        }

        views.apply {
            progress.progress = index
            when(index){
                0 -> {
                    imgStatus1.setImageResource(R.drawable.icon_waiting_order)
                    imgStatus2.setImageResource(R.drawable.icon_cooking_unselect)
                    imgStatus3.setImageResource(R.drawable.icon_delivering_unselect)
                    imgStatus4.setImageResource(R.drawable.icon_delivered_unselect)
                }
                1 ->{
                    imgStatus1.setImageResource(R.drawable.icon_waiting_order)
                    imgStatus2.setImageResource(R.drawable.icon_cooking)
                    imgStatus3.setImageResource(R.drawable.icon_delivering_unselect)
                    imgStatus4.setImageResource(R.drawable.icon_delivered_unselect)
                }
                2 ->{
                    imgStatus1.setImageResource(R.drawable.icon_waiting_order)
                    imgStatus2.setImageResource(R.drawable.icon_cooking)
                    imgStatus3.setImageResource(R.drawable.icon_delivering)
                    imgStatus4.setImageResource(R.drawable.icon_delivered_unselect)
                }
                3 ->{
                    imgStatus1.setImageResource(R.drawable.icon_waiting_order)
                    imgStatus2.setImageResource(R.drawable.icon_cooking)
                    imgStatus3.setImageResource(R.drawable.icon_delivering)
                    imgStatus4.setImageResource(R.drawable.icon_delivered)
                }
                4 ->{
                    imgStatus1.setImageResource(R.drawable.icon_waiting_order)
                    imgStatus2.setImageResource(R.drawable.icon_cooking)
                    imgStatus3.setImageResource(R.drawable.icon_delivering)
                    imgStatus4.setImageResource(R.drawable.icon_delivered_unselect)
                }
            }
        }
    }
}