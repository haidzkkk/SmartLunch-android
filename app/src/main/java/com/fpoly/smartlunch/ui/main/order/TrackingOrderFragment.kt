package com.fpoly.smartlunch.ui.main.order

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.ClusterMarker
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.UserLocation
import com.fpoly.smartlunch.databinding.FragmentTrackingOrderBinding
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ultis.MyClusterManagerRenderer
import com.fpoly.smartlunch.ultis.Status.avatar_shipper_default
import com.fpoly.smartlunch.ultis.Status.collection_user_locations
import com.fpoly.smartlunch.ultis.showUtilDialogWithCallback
import com.fpoly.smartlunch.ultis.startToDetailPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.clustering.ClusterManager

class TrackingOrderFragment : PolyBaseFragment<FragmentTrackingOrderBinding>(), OnMapReadyCallback {
    private val productViewModel: ProductViewModel by activityViewModel()
    private lateinit var mGoogleMap: GoogleMap
    private var mMapBoundary: LatLngBounds? = null
    private var clusterMarker: ClusterMarker? = null
    private var mClusterManager: ClusterManager<ClusterMarker>? = null
    private var currentOrder: OrderResponse? = null
    private var currentShipperId: String? = null
    private lateinit var mDb: FirebaseFirestore
    private var supportMapFragment: SupportMapFragment? = null
    private var mClusterManagerRenderer: MyClusterManagerRenderer? = null
    private val displaySize = DisplayMetrics()
    private lateinit var bottomBehavior: BottomSheetBehavior<LinearLayout>

    private val locationUpdateInterval: Long = 3000
    private val locationUpdateHandler = Handler(Looper.getMainLooper())
    private val locationUpdateRunnable = object : Runnable {
        override fun run() {
            currentShipperId?.let { getShipperLocation(it) }
            locationUpdateHandler.postDelayed(this, locationUpdateInterval)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGoogleMap()
        setupAppBar()
        setupBottomSheetBehavior()
        listenEvent()
    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    private fun setupAppBar() {
        views.appBar.apply {
            btnBackToolbar.visibility=View.VISIBLE
            tvTitleToolbar.text="Theo dõi đơn hàng"
        }
    }

    private fun setupBottomSheetBehavior() {
        requireActivity().windowManager.defaultDisplay.getMetrics(displaySize)
        bottomBehavior = BottomSheetBehavior.from(views.layoutTrackingOrder).apply {
            this.state = BottomSheetBehavior.STATE_COLLAPSED
            this.isHideable = false
            this.peekHeight = (displaySize.heightPixels * 0.1).toInt()
        }
    }

    private fun initGoogleMap() {
        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_view_tracking) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
        mDb = FirebaseFirestore.getInstance()
    }

    override fun onResume() {
        super.onResume()
        supportMapFragment?.onResume()
        if (checkPermissionRound2()) {
            onStartTracking()
        }
    }

    private fun checkPermissionRound2(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.showUtilDialogWithCallback(
                Notify(
                    "Yêu cầu quyền",
                    "bạn chưa cho phép quyền sử dụng vị trí",
                    "Vào cài đặt để cấp quyền",
                    R.raw.animation_successfully
                )
            ) {
                activity?.startToDetailPermission()
            }
            return false
        }
        return true
    }

    override fun onPause() {
        supportMapFragment?.onPause()
        super.onPause()
        onStopTracking()
    }

    override fun onDestroy() {
        supportMapFragment?.onDestroy()
        super.onDestroy()
        onStopTracking()
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTrackingOrderBinding {
        return FragmentTrackingOrderBinding.inflate(inflater, container, false)
    }

    override fun invalidate() {
        withState(productViewModel) {
            when (it.addOrder) {
                is Success -> {
                    currentOrder = it.addOrder.invoke()
                    currentOrder?.shipperId?.let { shipperId ->
                        currentShipperId = shipperId
                        getShipperLocation(shipperId)
                    }
                }
                is Fail -> {}
                else -> {}
            }
        }
    }

    private fun getShipperLocation(shipperId: String) {
        val userLocationCollection = mDb.collection(collection_user_locations).document(shipperId)
        userLocationCollection.get()
            .addOnSuccessListener { document ->
                val shipperLocation = document.toObject(UserLocation::class.java)
                shipperLocation?.let {
                    updateMarkerWithNewLocation(it)
                    updateShipperContactLayout(it)
                }
            }
            .addOnFailureListener { }
    }

    private fun updateShipperContactLayout(shipperLocation: UserLocation) {
        views.apply {
            shipperName.text = "${shipperLocation.user?.last_name} ${shipperLocation.user?.first_name}"
            phone.text = "${shipperLocation.user?.phone}"
            trackingToolbar.progressView3.visibility = View.GONE
            trackingToolbar.deliveredIcon.visibility = View.GONE
        }
    }

    private fun updateMarkerWithNewLocation(userLocation: UserLocation) {
        mClusterManager?.let { clusterManager ->
            userLocation.geoPoint?.let { geoPoint ->
                val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)
                clusterMarker = userLocation.user?.let {
                    ClusterMarker(
                        latLng,
                        it.first_name ?: "",
                        it.email ?: "",
                        it.avatar?.url ?: avatar_shipper_default,
                        it
                    )
                }
                clusterManager.clearItems()
                clusterMarker?.let {
                    clusterManager.addItem(it)
                }
                clusterManager.cluster()

                if (mMapBoundary == null) {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                }
                setCameraView(userLocation)
            }
        }
    }

    private fun setCameraView(mUserPosition: UserLocation) {
        val latLng = mUserPosition.geoPoint?.let { LatLng(it.latitude, it.longitude) }
        val offset = 0.01
        val bottomBoundary = latLng?.latitude?.minus(offset)
        val leftBoundary = latLng?.longitude?.minus(offset)
        val topBoundary = latLng?.latitude?.plus(offset)
        val rightBoundary = latLng?.longitude?.plus(offset)

        mMapBoundary = LatLngBounds(
            LatLng(bottomBoundary!!, leftBoundary!!),
            LatLng(topBoundary!!, rightBoundary!!)
        )

        if (mGoogleMap.cameraPosition.zoom < 14) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary!!, 0))
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mGoogleMap.isMyLocationEnabled = true
        mClusterManager = ClusterManager(context, mGoogleMap)
        mGoogleMap.setOnCameraIdleListener(mClusterManager)

        if (mClusterManagerRenderer == null) {
            mClusterManagerRenderer =
                MyClusterManagerRenderer(requireContext(), mGoogleMap, mClusterManager!!)
            mClusterManager?.renderer = mClusterManagerRenderer
        }
    }

    private fun onStartTracking() {
        locationUpdateHandler.postDelayed(locationUpdateRunnable, locationUpdateInterval)
    }

    private fun onStopTracking() {
        locationUpdateHandler.removeCallbacks(locationUpdateRunnable)
    }
}
