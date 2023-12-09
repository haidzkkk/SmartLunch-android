package com.fpoly.smartlunch.ui.payment.address

import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.AddressRequest
import com.fpoly.smartlunch.data.model.District
import com.fpoly.smartlunch.data.model.Province
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.model.Ward
import com.fpoly.smartlunch.databinding.FragmentAddAddressBinding
import com.fpoly.smartlunch.ui.main.profile.UserViewAction
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
import com.fpoly.smartlunch.ultis.checkNull
import com.fpoly.smartlunch.ultis.checkPhoneNumberValid
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class AddAddressFragment : PolyBaseFragment<FragmentAddAddressBinding>(), OnMapReadyCallback {

    var curentUser: User? = null

    lateinit var adapterSpinnerProvince: ArrayAdapter<Province>
    lateinit var adapterSpinnerDistrict: ArrayAdapter<District>
    lateinit var adapterSpinnerWard: ArrayAdapter<Ward>

    var listProvince: ArrayList<Province> = arrayListOf()
    var listDistrict: ArrayList<District> = arrayListOf()
    var listWard: ArrayList<Ward> = arrayListOf()

    var curenProvince: Province? = null
    var curenDistrict: District? = null
    var curenWard: Ward? = null

    var curentLatitude: Double? = null
    var curentLongidute: Double? = null

    private val paymentViewModel: PaymentViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    lateinit var gMap: GoogleMap

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddAddressBinding = FragmentAddAddressBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initSpinner()
        setupMap()
        lisstenEvent()
    }

    private fun initUI() {
        views.layoutHeader.tvTitleToolbar.text = "Thêm địa chỉ"
        views.layoutHeader.btnBackToolbar.isVisible = true
        curentUser = withState(userViewModel){ it.asyncCurrentUser.invoke()}
    }

    private fun initSpinner() {
        adapterSpinnerProvince = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listProvince)
        views.layoutSpinnerTinh.spinner.adapter = adapterSpinnerProvince

        adapterSpinnerDistrict = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listDistrict)
        views.layoutSpinnerHuyen.spinner.adapter = adapterSpinnerDistrict

        adapterSpinnerWard = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listWard)
        views.layoutSpinnerXa.spinner.adapter = adapterSpinnerWard

        paymentViewModel.handle(PaymentViewAction.GetProvinceAddress)
        handleResetSpinner(true, true, true)
    }

    private fun lisstenEvent() {
        views.layoutHeader.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
        views.btnAccept.setOnClickListener{
            handleGetAddressLocation()
            handlePostAddress()
        }

        views.layoutSpinnerTinh.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                handleResetSpinner(false, true, true)
                views.edtNote.setText("")

                if (position > 0){
                    curenProvince = listProvince.get(position)
                    handleGetAddressLocation()
                }else{
                    curenProvince = null
                    handleGetAddressLocation()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        views.layoutSpinnerHuyen.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                paymentViewModel.handle(PaymentViewAction.GetWardAddress(listDistrict.get(position).district_id))
                handleResetSpinner(false, false, true)
                views.edtNote.setText("")

                if (position > 0){
                    curenDistrict = listDistrict.get(position)
                    handleGetAddressLocation()
                }else{
                    curenDistrict = null
                    handleGetAddressLocation()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        views.layoutSpinnerXa.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                views.edtNote.setText("")
                if (position > 0){
                    curenWard = listWard.get(position)
                    handleGetAddressLocation()
                }else{
                    curenWard = null
                    handleGetAddressLocation()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

    }

    private fun handleGetAddressLocation() {
        var geocoder = Geocoder(requireContext())
        var listLocation: List<android.location.Address>? = null

        try {
            listLocation = geocoder.getFromLocationName("${views.edtNote.text.toString().trim()}, ${curenWard?.ward_name}, ${curenDistrict?.district_name}, ${curenProvince?.province_name}", 1)
            Log.e("AddAddressFragment", "handleGetAddressLocation: ${listLocation}", )
            if (!listLocation.isNullOrEmpty()){
                curentLatitude = listLocation.get(0).latitude
                curentLongidute = listLocation.get(0).longitude

                var latLng = LatLng(curentLatitude!!, curentLongidute!!)
                gMap.clear()
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
                gMap.addMarker(MarkerOptions().position(latLng))
            }else{
                gMap.clear()
                curentLatitude = null
                curentLongidute = null
            }

        }catch (e: IOException){
            gMap.clear()
            curentLatitude = null
            curentLongidute = null
            Log.e("AddAddressFragment", "handleGetAddressLocation: $e", )
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(requireContext(), "Tim dia chi lỗi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMap() {
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_view_payment) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
    }

    private fun handleResetSpinner(isResetProvince: Boolean, isResetDistrict: Boolean,isResetWard: Boolean,){
        if (isResetProvince){
            listProvince.clear()
            listProvince.add(Province("-1", "--- Chọn tỉnh / thành phố ---", ""))
            adapterSpinnerProvince.notifyDataSetChanged()
        }

        if (isResetDistrict){
            listDistrict.clear()
            listDistrict.add(District("-1", "--- Chọn quận / huyện ---", "", ""))
            adapterSpinnerDistrict.notifyDataSetChanged()
        }

        if (isResetWard){
            listWard.clear()
            listWard.add(Ward("-1", "--- Chọn phường / xã / thị trấn ---", "", ""))
            adapterSpinnerWard.notifyDataSetChanged()
        }
    }

    private fun handlePostAddress() {
        if (views.edtName.checkNull(requireActivity().resources)
            or views.edtPhone.checkNull(requireActivity().resources) or views.tilPhone.checkPhoneNumberValid(requireActivity().resources)
            || curenProvince == null || curenDistrict == null
            || ( curenWard == null && views.edtNote.checkNull(requireActivity().resources) )){
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }else{
            var strAddressLine = ""

            val edtNoteText = views.edtNote.text.toString().trim()
            val wardName = curenWard?.ward_name
            if (edtNoteText.isNotEmpty() && wardName?.isNotEmpty() == true) {
                strAddressLine = "$edtNoteText, $wardName, ${curenDistrict?.district_name}, ${curenProvince?.province_name}"
            } else {
                strAddressLine = "${edtNoteText}${if (edtNoteText.isNotEmpty() && wardName.isNullOrEmpty()) "" else wardName}, ${curenDistrict?.district_name}, ${curenProvince?.province_name}"
            }

            var addressRequest = AddressRequest(
                views.edtName.text.toString(),
                views.edtPhone.text.toString(),
                strAddressLine,
                curentLatitude ?: 0.0,
                curentLongidute ?: 0.0,
                )
            userViewModel.handle(UserViewAction.AddAddress(addressRequest))
        }
    }

    override fun invalidate() {
        withState(userViewModel){
            paymentViewModel.returnShowLoading(it.asyncCreateAddress is Loading)

            when(it.asyncCreateAddress){
                is Success ->{
                    Toast.makeText(requireContext(), "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                    it.asyncCreateAddress = Uninitialized
                }
                is Fail ->{
                    Toast.makeText(requireContext(), "Thêm địa chỉ thất bại", Toast.LENGTH_SHORT).show()
                }
                else ->{

                }
            }
        }

        withState(paymentViewModel){
            when(it.asyncListProvince){
                is Success ->{
                    var listProvinceState = it.asyncListProvince.invoke()?.results
                    if (listProvinceState != null && listProvinceState.isNotEmpty()){
                        this.listProvince.addAll(listProvinceState)
                        adapterSpinnerProvince.notifyDataSetChanged()
                        it.asyncListProvince = Uninitialized
                    }
                }
                else ->{

                }
            }
            when(it.asyncListDistrict){
                is Success ->{
                    var listDistrictState = it.asyncListDistrict.invoke()?.results
                    if (listDistrictState != null && it.asyncListWard.invoke()?.results.isNullOrEmpty()
                    ){
                        this.listDistrict.addAll(listDistrictState)
                        adapterSpinnerProvince.notifyDataSetChanged()
                        it.asyncListDistrict = Uninitialized
                    }
                }
                else ->{

                }
            }
            when(it.asyncListWard){
                is Success ->{
                    var listWardState = it.asyncListWard.invoke()?.results
                    if (listWardState != null && listWardState.isNotEmpty()){
                        this.listWard.addAll(listWardState)
                        adapterSpinnerWard.notifyDataSetChanged()
                        it.asyncListWard = Uninitialized
                    }
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