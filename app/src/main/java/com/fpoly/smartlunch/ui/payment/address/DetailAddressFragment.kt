package com.fpoly.smartlunch.ui.payment.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Address
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.databinding.FragmentAddAddressBinding
import com.fpoly.smartlunch.ui.main.profile.UserViewAction
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
import com.fpoly.smartlunch.ultis.showUtilDialogWithCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

class DetailAddressFragment: PolyBaseFragment<FragmentAddAddressBinding>(), OnMapReadyCallback {

    var gMap: GoogleMap? = null
    var curentAddress: Address? = null

    private val userViewModel: UserViewModel by activityViewModel()
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAddAddressBinding {
        return FragmentAddAddressBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        listenEvent()
    }

    private fun initUI() {
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_view_payment) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)

        views.edtName.isEnabled = false
        views.edtPhone.isEnabled = false
        views.edtNote.isEnabled = false

        views.layoutSpinnerTinh.root.isVisible = false
        views.layoutSpinnerHuyen.root.isVisible = false
        views.layoutSpinnerXa.root.isVisible = false

        views.btnAccept.text = "Xóa"
        views.layoutHeader.tvTitleToolbar.text = "Chi tiết địa chỉ"
        views.layoutHeader.btnBackToolbar.isVisible = true
    }

    private fun listenEvent() {
        views.layoutHeader.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
        views.btnAccept.setOnClickListener{
            if (curentAddress != null){
                requireActivity().showUtilDialogWithCallback(
                    Notify("Thông báo", "", "Bạn có muôn xóa địa chỉ này không", R.raw.succes_gif)){
                    userViewModel.handle(UserViewAction.DeleteAddressById(curentAddress?._id ?: ""))
                }
            }else{
                Toast.makeText(requireContext(), "Không có địa chỉ", Toast.LENGTH_SHORT).show()
            }   

        }
    }

    override fun onDestroy() {
        gMap?.clear()
        withState(userViewModel){
            it.asyncAddress = Uninitialized
        }
        super.onDestroy()
    }

    override fun invalidate(){
        withState(userViewModel){
            when(it.asyncAddress){
                is Success ->{
                    curentAddress = it.asyncAddress.invoke()
                    if (curentAddress != null){
                        views.edtName.setText(curentAddress!!.recipientName)
                        views.edtPhone.setText(curentAddress!!.phoneNumber)
                        views.edtNote.setText(curentAddress!!.addressLine)

                        gMap?.clear()
                        gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(curentAddress!!.toLatLng(), 15F))
                        gMap?.addMarker(MarkerOptions().position(curentAddress!!.toLatLng()))
                    }
                }
                else ->{

                }
            }

            when(it.asyncDeleteAddress){
                is Success ->{
                    Toast.makeText(requireContext(), "Xóa địa chỉ thành công", Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                    it.asyncDeleteAddress = Uninitialized
                }
                is Fail ->{
                    Toast.makeText(requireContext(), "Xóa địa chỉ thất bại", Toast.LENGTH_SHORT).show()
                }
                else ->{

                }
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
    }
}