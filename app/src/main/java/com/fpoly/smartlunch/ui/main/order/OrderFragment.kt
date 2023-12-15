package com.fpoly.smartlunch.ui.main.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentOrderBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OrderFragment : PolyBaseFragment<FragmentOrderBinding>() {
    private lateinit var tabLayout: TabLayout
    private val productViewModel: ProductViewModel by activityViewModel()

    companion object {
        const val TAG = "OrderFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        listenEvent()
    }

    private fun setupUI() {
        views.appBar.tvTitleToolbar.text = "Đơn hàng"
        tabLayout = views.tabLayout
        val viewPager = views.viewPager
        val adapter = OrderViewPagerAdapter(childFragmentManager, this.lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Chờ xác nhận"
                }

                1 -> {
                    tab.text = "Xác nhận"
                }

                2 -> {
                    tab.text = "Đang giao"
                }

                3 -> {
                    tab.text = "Đã giao hàng"
                }

                4 -> {
                    tab.text = "Đã hủy đơn"
                }
            }
        }.attach()
    }

    private fun listenEvent() {
        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetAllOrderByUserId)
        }
    }

    override fun invalidate() {
        withState(productViewModel) {
            views.swipeLoading.isRefreshing =
                it.asyncCompleted is Loading || it.asyncUnconfirmed is Loading || it.asyncConfirmed is Loading || it.asyncDelivering is Loading || it.asyncCancelled is Loading
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentOrderBinding =
        FragmentOrderBinding.inflate(inflater, container, false)
}
