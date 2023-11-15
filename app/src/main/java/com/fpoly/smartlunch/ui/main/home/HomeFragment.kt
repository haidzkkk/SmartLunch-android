package com.fpoly.smartlunch.ui.main.home


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.UserLocation
import com.fpoly.smartlunch.databinding.FragmentHomeBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProductVer
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductEvent
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ultis.checkRequestPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject

class HomeFragment @Inject constructor() : PolyBaseFragment<FragmentHomeBinding>() {
    companion object {
        const val TAG = "HomeFragment"
    }

    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()

    private lateinit var adapter: AdapterProduct
    private lateinit var adapterver: AdapterProductVer

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mUserLocation: UserLocation? = null
    private var mLocationPermissionGranted = false

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

    private fun initUi() {
        updateCart()
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
    }

    private fun setupLocation() {
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun updateCart() {
        productViewModel.handle(ProductAction.GetOneCartById)
    }

    private fun listenEvent() {
        productViewModel.observeViewEvents {
            handleViewEvent(it)
        }

        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetListProduct)
            productViewModel.handle(ProductAction.GetListTopProduct)
        }

        views.btnDefault.setOnClickListener {
            openCategoryBottomSheet()
        }
        views.floatBottomSheet.setOnClickListener {
            openCartBottomSheet()
        }
    }

    private fun handleViewEvent(event: ProductEvent) {
        when (event) {
            is ProductEvent.UpdateCart -> updateCart()
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
        homeViewModel.returnDetailProductFragment()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(true)
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
            when (it.getOneCartById) {
                is Success -> {
                    if (it.getOneCartById.invoke()?.products?.size!! > 0) {
                        views.layoutCart.visibility = View.VISIBLE
                    } else {
                        views.layoutCart.visibility = View.GONE
                    }
                }

                is Fail -> {
                    views.layoutCart.visibility = View.GONE
                }

                else -> {
                    views.layoutCart.visibility = View.GONE
                }
            }
        }
    }

}