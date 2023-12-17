package com.fpoly.smartlunch.ui.main.profile

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Address
import com.fpoly.smartlunch.databinding.FragmentIntroductBinding
import com.fpoly.smartlunch.ui.main.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions

class IntroductFragment : PolyBaseFragment<FragmentIntroductBinding>(), OnMapReadyCallback {
    private val userViewModel: UserViewModel by activityViewModel()
    var gMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        lisstenEvent()
    }

    private fun initUI() {
        views.layoutHeader.apply {
            btnBackToolbar.visibility = View.VISIBLE
            tvTitleToolbar.text = getString(R.string.introduce)
        }
        userViewModel.handle(UserViewAction.GetAddressAdmin)
    }

    private fun lisstenEvent() {
        views.layoutHeader.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun handleData(address: Address?){
        views.tvAddressLine.text = address?.addressLine

        views.tvPhone.text = address?.phoneNumber
        Linkify.addLinks(views.tvPhone, Linkify.PHONE_NUMBERS)
        views.tvPhone.setLinkTextColor(requireContext().resources.getColor(R.color.black))

        gMap?.clear()
        gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(address!!.toLatLng(), 15F))
        gMap?.addMarker(MarkerOptions().position(address!!.toLatLng()))
    }

    override fun invalidate() {
        withState(userViewModel){
            when(it.asyncAddressAdmin){
                is Success ->{
                    handleData(it.asyncAddressAdmin.invoke())
                }

                else -> {}
            }
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentIntroductBinding.inflate(layoutInflater)

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
    }
}