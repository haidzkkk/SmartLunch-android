package com.fpoly.smartlunch.ui.main.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.databinding.FragmentOrderDetailBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ultis.Status

class OrderDetailFragment : PolyBaseFragment<FragmentOrderDetailBinding>() {
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private var currentOrder: OrderResponse? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        listenEvent()
    }

    private fun initUI() {

    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
        views.followOrderText.setOnClickListener {
            homeViewModel.returnTrackingOrderFragment()
        }
        views.btnConfirmReceived.setOnClickListener{
            homeViewModel.returnProductReviewFragment()
        }
    }

    private fun setupUI(currentOrder: OrderResponse) {
        views.appBar.apply {
            btnBackToolbar.visibility = View.VISIBLE
            tvTitleToolbar.text = getString(R.string.order)
        }
        views.apply {
            recipientName.text = currentOrder.address.recipientName
            orderTimeValue.text = currentOrder.createdAt
            pickupTimeValue.text =currentOrder.updatedAt
            orderCodeValue.text = currentOrder._id
            tvStatus.text = currentOrder.status.status_name
            location.text = currentOrder.address.addressLine
            phoneNumber.text = currentOrder.address.phoneNumber

            followOrderText.isVisible = currentOrder.status._id == Status.DELIVERING_STATUS
            btnConfirmReceived.isEnabled = currentOrder.status._id == Status.SUCCESS_STATUS
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
        when(it.addOrder){
            is Success -> {
                currentOrder= it.addOrder.invoke()
                currentOrder?.let { currentOrder -> setupUI(currentOrder) }
            }
            is Fail -> {
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
            }
        }
    }
}