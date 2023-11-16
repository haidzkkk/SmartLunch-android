package com.fpoly.smartlunch.ui.main.order

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.ClusterMarker
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.UserLocation
import com.fpoly.smartlunch.databinding.FragmentTrackingOrderBinding
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ultis.MyClusterManagerRenderer
import com.fpoly.smartlunch.ultis.MyConfigNotifi
import com.fpoly.smartlunch.ultis.Status
import com.fpoly.smartlunch.ultis.Status.MAPVIEW_BUNDLE_KEY
import com.fpoly.smartlunch.ultis.Status.collection_user_locations
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.clustering.ClusterManager

class TrackingOrderFragment : PolyBaseFragment<FragmentTrackingOrderBinding>(), OnMapReadyCallback {
    private val productViewModel: ProductViewModel by activityViewModel()

    private lateinit var mGoogleMap: GoogleMap
    private var mMapBoundary: LatLngBounds? = null
    private var mUserPosition: UserLocation? = null
    private var currentOrder: OrderResponse? = null

    private lateinit var mDb: FirebaseFirestore
    private var supportMapFragment: SupportMapFragment? = null
    private var mClusterManager: ClusterManager<ClusterMarker>? = null
    private var mClusterManagerRenderer: MyClusterManagerRenderer? = null
    private var mClusterMarkers: ArrayList<ClusterMarker> = ArrayList()

    private val mHandler: Handler = Handler()
    private var mRunnable: Runnable? = null
    private var mUserLocations = ArrayList<UserLocation>()

    companion object {
        private const val LOCATION_UPDATE_INTERVAL: Long = 3000
        private const val TAG = "TrackingOrderFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGoogleMap(savedInstanceState)
        lisstenEvent()
    }

    private fun lisstenEvent() {
        views.btnChat.setOnClickListener{
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putString("type", MyConfigNotifi.TYPE_CHAT)
                    putString("idUrl", currentOrder?.shipperId ?: "") }
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_view_tracking) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
        mDb = FirebaseFirestore.getInstance()
    }

    private fun setUserPosition() {
        if (mUserPosition == null && currentOrder?.shipperId != null) {
            val locationRef: DocumentReference = mDb
                .collection(collection_user_locations)
                .document(currentOrder!!._id)

            locationRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Dữ liệu tồn tại, bạn có thể giải mã và sử dụng nó
                        mUserPosition = documentSnapshot.toObject(UserLocation::class.java)
                        mUserLocations.add(mUserPosition!!)
                    } else {
                        // Tài liệu không tồn tại
                        Log.d("TAG", "Tài liệu không tồn tại")
                    }
                }
                .addOnFailureListener { exception ->
                    // Xử lý lỗi nếu có
                    Log.e("TAG", "Lỗi khi đọc dữ liệu từ Firestore", exception)
                }
        }
    }


    private fun startUserLocationsRunnable() {
        mHandler.postDelayed(Runnable {
            retrieveUserLocations()
            mHandler.postDelayed(mRunnable!!, LOCATION_UPDATE_INTERVAL)
        }.also { mRunnable = it }, LOCATION_UPDATE_INTERVAL)
    }

    private fun stopLocationUpdates() {
        mHandler.removeCallbacks(mRunnable!!)
    }

    private fun retrieveUserLocations() {
        try {
            for (clusterMarker in mClusterMarkers) {
                val userLocationRef = FirebaseFirestore.getInstance()
                    .collection(collection_user_locations)
                    .document(clusterMarker.user._id)
                userLocationRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val updatedUserLocation = task.result.toObject(
                            UserLocation::class.java
                        )
                        // update the location
                        for (i in mClusterMarkers.indices) {
                            try {
                                if (mClusterMarkers[i].user._id == updatedUserLocation!!.user!!._id) {
                                    val updatedLatLng = LatLng(
                                        updatedUserLocation.geoPoint!!.latitude,
                                        updatedUserLocation.geoPoint!!.longitude
                                    )
                                    mClusterMarkers[i].position = updatedLatLng
                                    mClusterManagerRenderer?.setUpdateMarker(mClusterMarkers[i])
                                }
                            } catch (e: java.lang.NullPointerException) {
                                Log.e(
                                    TAG,
                                    "retrieveUserLocations: NullPointerException: " + e.message
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: IllegalStateException) {
            Log.e(
                TAG,
                "retrieveUserLocations: Fragment was destroyed during Firestore query. Ending query." + e.message
            )
        }
    }

    private fun addMapMarkers() {
        if (mClusterManager == null) {
            mClusterManager =
                ClusterManager<ClusterMarker>(requireActivity().applicationContext, mGoogleMap)
        }
        if (mClusterManagerRenderer == null) {
            mClusterManagerRenderer = MyClusterManagerRenderer(
                requireActivity(),
                mGoogleMap,
                mClusterManager!!
            )
            mClusterManager?.renderer = mClusterManagerRenderer
        }
        for (userLocation in mUserLocations) {
            Log.d(TAG, "addMapMarkers: location: " + userLocation.geoPoint.toString())
            try {
                val snippet =
                    "Determine route to " + userLocation.user!!.first_name + "?"
                var avatar: String? = ""
                try {
                    avatar = userLocation.user!!.avatar!!.url
                } catch (e: NumberFormatException) {
                    Log.d(
                        TAG,
                        "addMapMarkers: no avatar for " + userLocation.user!!.first_name + ", setting default."
                    )
                }
                val newClusterMarker = ClusterMarker(
                    LatLng(
                        userLocation.geoPoint!!.latitude,
                        userLocation.geoPoint!!.longitude
                    ),
                    userLocation.user!!.first_name,
                    snippet,
                    avatar!!,
                    userLocation.user!!
                )
                mClusterManager?.addItem(newClusterMarker)
                mClusterMarkers.add(newClusterMarker)
            } catch (e: NullPointerException) {
                Log.e(TAG, "addMapMarkers: NullPointerException: " + e.message)
            }
        }
        mClusterManager?.cluster()

    }

    private fun handleSetAnimationCamera(latLng: LatLng) {
        mGoogleMap.clear()
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17F))
        mGoogleMap.addMarker(MarkerOptions().position(latLng))
    }

    override fun onResume() {
        super.onResume()
        supportMapFragment!!.onResume()
        startUserLocationsRunnable()
    }

    override fun onStart() {
        super.onStart()
        supportMapFragment!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        supportMapFragment!!.onStop()
    }

    override fun onPause() {
        supportMapFragment!!.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        supportMapFragment!!.onDestroy()
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        supportMapFragment!!.onLowMemory()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingOrderBinding {
        return FragmentTrackingOrderBinding.inflate(inflater, container, false)
    }

    override fun invalidate(): Unit = withState(productViewModel) {
        when (it.addOrder) {
            is Success -> {
                currentOrder = it.addOrder.invoke()
                setUserPosition()
                handleStateProgress()
            }

            is Fail -> {
            }

            else -> {}
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0
        addMapMarkers()
        handleSetAnimationCamera(
            LatLng(
                22.7615314,
                105.34824599999999
            )
        )
    }

    fun handleStateProgress(){
        if (currentOrder == null) return

        var index = when(currentOrder!!.status._id){
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