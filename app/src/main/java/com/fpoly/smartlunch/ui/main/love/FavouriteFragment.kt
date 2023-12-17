package com.fpoly.smartlunch.ui.main.love

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentFavouriteBinding
import com.fpoly.smartlunch.ui.main.love.Adapter.FavouriteViewPagerAdapter
import com.fpoly.smartlunch.ui.main.order.OrderViewPagerAdapter
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FavouriteFragment : PolyBaseFragment<FragmentFavouriteBinding>() {
    private val productViewModel: ProductViewModel by activityViewModel()
    private lateinit var tabLayout: TabLayout
    companion object{
        const val TAG = "FavouriteFragment"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        listenEvent()
    }

    private fun setupUI() {
        views.appBar.tvTitleToolbar.text = getText(R.string.favourite)
        tabLayout = views.tabLayout
        val viewPager = views.viewPager
        val adapter = FavouriteViewPagerAdapter(childFragmentManager, this.lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.favourite)
                }
                1 -> {
                    tab.text = getString(R.string.history)
                }
            }
        }.attach()
    }

    private fun listenEvent() {
        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetAllFavouriteProduct)
            productViewModel.handle(ProductAction.GetAllHistoryProduct)
        }
    }

    override fun invalidate():Unit = withState(productViewModel) {
        views.swipeLoading.isRefreshing = it.asyncFavourites is Loading
    }
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavouriteBinding
            = FragmentFavouriteBinding.inflate(inflater, container, false)
}