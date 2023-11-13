package com.fpoly.smartlunch.ui.main.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class OrderDetailFragment : PolyBaseFragment<FragmentOrderDetailBinding>() {
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private var currentOrder: OrderResponse? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenEvent()
    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        views.followOrderText.setOnClickListener {
            homeViewModel.returnTrackingOrderFragment()
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
            location.text = currentOrder.address.addressLine
            phoneNumber.text = currentOrder.address.phoneNumber
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
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

}