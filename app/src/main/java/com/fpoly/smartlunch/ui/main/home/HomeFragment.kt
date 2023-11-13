package com.fpoly.smartlunch.ui.main.home


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentHomeBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProductVer
import com.fpoly.smartlunch.ui.main.home.adapter.BannerAdapter
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductEvent
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import javax.inject.Inject

class HomeFragment @Inject constructor() : PolyBaseFragment<FragmentHomeBinding>() {
    companion object {
        const val TAG = "HomeFragment"
    }

    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var adapter: AdapterProduct
    private lateinit var adapterver: AdapterProductVer

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mRunable: Runnable = Runnable {
        var curentPosition = views.vpBanner.currentItem
        if (curentPosition == views.vpBanner.adapter!!.itemCount - 1){
            views.vpBanner.currentItem = 0
        }else{
            views.vpBanner.currentItem = (curentPosition + 1)
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        listenEvent()
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mRunable)
    }

    override fun onResume() {
        super.onResume()
        productViewModel.handle(ProductAction.GetOneCartById)
        homeViewModel.returnVisibleBottomNav(true)
        mHandler.postDelayed(mRunable, 3000)
    }

    private fun initUi() {
        //setup rcyH
        adapter = AdapterProduct {
            onItemProductClickListener(it)
        }
        views.recyclerViewHoz.adapter = adapter

        //setup rcyV
        adapterver = AdapterProductVer {
            onItemProductClickListener(it)
        }
        views.recyclerViewVer.adapter = adapterver

        // banner
        bannerAdapter = BannerAdapter {

        }
        bannerAdapter.setData(null)
        views.vpBanner.adapter = bannerAdapter
        views.dotsBanner.attachTo(views.vpBanner)
        views.vpBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandler.removeCallbacks(mRunable)
                mHandler.postDelayed(mRunable, 3000)
            }
        })
    }

    private fun listenEvent() {
        productViewModel.observeViewEvents {
            handleViewEvent(it)
        }

        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetOneCartById)
            productViewModel.handle(ProductAction.GetListProduct)
            productViewModel.handle(ProductAction.GetListTopProduct)
            homeViewModel.handle(HomeViewAction.getBanner)
        }

        views.btnDefault.setOnClickListener {
            openCategoryBottomSheet()
        }
        views.floatBottomSheet.setOnClickListener {
            openCartBottomSheet()
        }
    }

    private fun handleViewEvent(event: ProductEvent) {
        when(event){
            else -> {}
        }
    }

    private fun openCartBottomSheet() {
        val bottomSheetFragment = HomeBottomSheet()
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    private fun openCategoryBottomSheet() {
        val bottomSheetFragment = HomeBottomSheetCategory()
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    private fun onItemProductClickListener(productId:String) {
        productViewModel.handle(ProductAction.GetDetailProduct(productId))
        productViewModel.handle(ProductAction.GetListCommentsLimit(productId))
        homeViewModel.returnDetailProductFragment()
    }


    override fun invalidate() {
        withState(homeViewModel){
            when(it.asyncBanner){
                is Success ->{
                    bannerAdapter.setData(it.asyncBanner.invoke())
                }
                is Fail ->{
                    bannerAdapter.setData(null)
                }
                else ->{

                }
            }
        }

        withState(productViewModel) {
            views.swipeLoading.isRefreshing = it.isSwipeLoading

            when (it.products) {
                is Success -> {
                    views.floatBottomSheet.isVisible = true
                    adapterver.setData(it.products.invoke()?.docs)
                }
                else -> {
                }
            }

            when (it.asyncTopProduct) {
                is Success -> {
                    adapter.setData(it.asyncTopProduct.invoke())
                }
                else -> {
                }
            }
            when (it.curentCartResponse) {
                is Success -> {
                    if (it.curentCartResponse.invoke()?.products?.size!! > 0) {
                        views.layoutCart.visibility = View.VISIBLE
                    }else{
                        views.layoutCart.visibility = View.GONE
                    }
                }
                else -> {
                    views.layoutCart.visibility = View.GONE
                }
            }
        }
    }

}