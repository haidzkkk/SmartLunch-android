package com.fpoly.smartlunch.ui.main.home


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.PagingRequestProduct
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.SortPagingProduct
import com.fpoly.smartlunch.data.model.UserLocation
import com.fpoly.smartlunch.databinding.FragmentHomeBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProductVer
import com.fpoly.smartlunch.ui.main.home.adapter.BannerAdapter
import com.fpoly.smartlunch.ui.main.home.adapter.ProductPaginationAdapter
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductEvent
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ultis.PaginationScrollListenner
import com.fpoly.smartlunch.ultis.checkRequestPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject

class HomeFragment @Inject constructor() : PolyBaseFragment<FragmentHomeBinding>(){
    companion object {
        const val TAG = "HomeFragment"
    }

    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var adapter: AdapterProduct
    private lateinit var adapterver: AdapterProductVer
    private lateinit var productAdapter: ProductPaginationAdapter

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mUserLocation: UserLocation? = null
    private var mLocationPermissionGranted = false
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
        setupLocation()
        listenEvent()
        setupCurrentLocation()
        checkGoogleServicesAndGPS()
    }

    private fun checkGoogleServicesAndGPS() {
        if (mLocationPermissionGranted) {
            getLastKnowLocation()
        } else {
            getLocationPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mRunable)
        productAdapter.notifyDataSetChanged()
    }

    private fun initUi() {
        //setup rcyH
        adapter = AdapterProduct(object: AdapterProduct.OnClickListenner{
            override fun onCLickItem(id: String) {
                onItemProductClickListener(id)
            }

            override fun onCLickSeeMore() {

            }
        })
        views.recyclerViewHoz.adapter = adapter

        //setup rcyV
        adapterver = AdapterProductVer {
            onItemProductClickListener(it)
        }
        views.recyclerViewVer.adapter = adapterver
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

        var linearLayoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductPaginationAdapter {

        }
        views.rcvProductAll.adapter = productAdapter
        views.rcvProductAll.layoutManager = linearLayoutManager
        views.rcvProductAll.addOnScrollListener(object : PaginationScrollListenner(linearLayoutManager){
            override fun loadMoreItems() {
                productAdapter.isLoadingOk = true
                productAdapter.curentPage += 1
                productViewModel.handle(ProductAction.GetListProduct(
                    PagingRequestProduct(2, null, null, productAdapter.curentPage, null))
                )
            }

            override fun isLoading(): Boolean {
                return productAdapter.isLoadingOk
            }

            override fun isLastPage(): Boolean {
                return productAdapter.isLastPage
            }
        })
//        ProductAction.GetListProduct(PagingRequestProduct(2, null, null, productAdapter.curentPage, null))
        Log.e("TAG", "onViewCreated: productAdapter -> ${productAdapter.curentPage}", )
    }

        private fun setupLocation() {
            mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
        }

        private fun listenEvent() {
            productViewModel.observeViewEvents {
                handleViewEvent(it)
            }

            views.swipeLoading.setOnRefreshListener {
                productViewModel.handle(ProductAction.GetOneCartById)
                productViewModel.handle(ProductAction.GetListProductRate(PagingRequestProduct(5, SortPagingProduct.rate, null, null, null)))
                productViewModel.handle(ProductAction.GetListTopProduct(PagingRequestProduct(5, SortPagingProduct.bought, null, null, null)))
                homeViewModel.handle(HomeViewAction.getBanner)
            }

            views.btnDefault.setOnClickListener {
                openCategoryBottomSheet()
            }
            views.floatBottomSheet.setOnClickListener {
                openCartBottomSheet()
            }
            views.notification.setOnClickListener {
                homeViewModel.returnNotificationFragment()
            }
        }

        private fun handleViewEvent(event: ProductEvent) {
            when (event) {
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

        private fun onItemProductClickListener(productId: String) {
            productViewModel.handle(ProductAction.GetDetailProduct(productId))
            productViewModel.handle(ProductAction.GetListSizeProduct(productId))
            productViewModel.handle(ProductAction.GetListCommentsLimit(productId))
            homeViewModel.returnDetailProductFragment()
        }

        override fun onResume() {
            super.onResume()
            productViewModel.handle(ProductAction.GetOneCartById)
            homeViewModel.returnVisibleBottomNav(true)
            mHandler.postDelayed(mRunable, 3000)
            if (mLocationPermissionGranted) {
                getLastKnowLocation()
            } else {
                getLocationPermission()
            }
        }

        private fun getLocationPermission() {
            checkRequestPermissions {
                mLocationPermissionGranted = it
            }
        }

        private fun setupAppBar(location: String) {
            views.currentLocation.text = location
        }

        private fun getLastKnowLocation() {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            mFusedLocationProviderClient?.lastLocation?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    if (location != null) {
                        val geoPoint = GeoPoint(location.latitude, location.longitude)
                        homeViewModel.handle(
                            HomeViewAction.GetCurrentLocation(
                                location.latitude,
                                location.longitude
                            )
                        )
                        mUserLocation?.apply {
                            this.geoPoint = geoPoint
                            this.timestamp = null
                        }
                    } else {
                        Log.e(TAG, "getLastKnowLocation: Last known location is null")
                    }
                } else {
                    Log.e(TAG, "getLastKnowLocation: Failed to get last known location")
                }
            }
        }


    private fun setupCurrentLocation(): Unit = withState(homeViewModel) {
        when (it.asyncGetCurrentLocation) {
            is Success -> {
                it.asyncGetCurrentLocation.invoke()?.let { location ->
                    val currentLocation: String =
                        " " + location.address.road + ", " + location.address.quarter + ", " + location.address.suburb
                    setupAppBar(currentLocation)
                }
            }

            is Fail -> {
                setupAppBar(" Đang tải...")
            }

            else -> {}
        }
    }

    override fun invalidate() {
      setupCurrentLocation()
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

            when (it.asyncProducts) {
                is Success -> {
                    productAdapter.isLoadingOk = false
                    productAdapter.setData(it.asyncProducts.invoke()?.docs)
                    it.asyncProducts = Uninitialized
                }
                else -> {
                }
            }

            when (it.productsRate) {
                is Success -> {
                    views.floatBottomSheet.isVisible = true
                    adapterver.setData(it.productsRate.invoke()?.docs)
                }
                else -> {
                }
            }

            when (it.asyncTopProduct) {
                is Success -> {
                    adapter.setData(it.asyncTopProduct.invoke()?.docs)
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
            when (it.asyncUnreadNotifications) {
                is Success -> {
                    if (it.asyncUnreadNotifications.invoke()?.size!! > 0) {
                        views.unreadNoti.visibility = View.VISIBLE
                        views.unreadNoti.text=
                            it.asyncUnreadNotifications.invoke()?.size.toString()
                    }else{
                        views.unreadNoti.visibility = View.GONE
                    }
                }
                else -> {
                    views.layoutCart.visibility = View.GONE
                }
            }
        }
    }

}
