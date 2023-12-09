package com.fpoly.smartlunch.ui.paynow.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Address
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentPayBinding
import com.fpoly.smartlunch.ui.main.profile.UserViewAction
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
import com.fpoly.smartlunch.ui.payment.payment.PaymentTypeBottomSheet
import com.fpoly.smartlunch.ui.paynow.PayNowViewAction
import com.fpoly.smartlunch.ui.paynow.PayNowViewModel
import com.fpoly.smartlunch.ultis.Status
import com.fpoly.smartlunch.ultis.formatCash
import com.fpoly.smartlunch.ultis.formatPaypal
import com.fpoly.smartlunch.ultis.showUtilDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.PurchaseUnit
import javax.inject.Inject

class PaymentNowFragment : PolyBaseFragment<FragmentPayBinding>(), OnMapReadyCallback {

    var mGoogleMap: GoogleMap? = null

    private val payNowViewModel: PayNowViewModel by activityViewModel()
    private val paymentViewModel: PaymentViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private var myCart: CartResponse? = null
    private var myAddress: Address? = null
    private var strNote: String = ""

    var addOrder: OrderResponse? = null

    @Inject
    lateinit var sessionManager: SessionManager

    companion object {
        const val ACTIVITY_PAY_REQUEST_CODE = 123
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        init()
        setupMap()
        listenEvent()
    }

    private fun setupAppBar() {
        views.toolbarPay.apply {
            btnBackToolbar.visibility = View.VISIBLE
            tvTitleToolbar.text = getString(R.string.pay)
        }
    }

    private fun init() {
        strNote = arguments?.getString("strNote").toString()
    }

    private fun setupLayoutAddress(){
        if (myAddress == null) return

        views.apply {
            address.text=myAddress?.addressLine
            phone.text=myAddress?.phoneNumber
        }

        if (mGoogleMap == null) return
        mGoogleMap!!.apply {
            clear()
            moveCamera(CameraUpdateFactory.newLatLngZoom(myAddress!!.toLatLng(), 12F))
            addMarker(MarkerOptions().position(myAddress!!.toLatLng()))
        }
    }
    private fun setupUi() {
        views.apply {
            extraCost.text = myCart?.total?.formatCash()
            discoutCost.text = (myCart?.totalCoupon ?: 0.0).formatCash()
            shipCost.text = (myAddress?.deliveryFee ?: 0.0).toInt().formatCash()
            couponCode.text = myCart?.couponId?._id
            total.text = ((myCart?.total ?: 0.0) - (myCart?.totalCoupon ?: 0.0) + (myAddress?.deliveryFee ?: 0.0)).formatCash()
        }
    }
    private fun setupMap() {
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_view_payment) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
    }

    private fun listenEvent() {
        views.swipeLoading.setOnRefreshListener {
            payNowViewModel.handle(PayNowViewAction.CheckUpdateCartLocal(myCart))
            userViewModel.handle(UserViewAction.GetListAddress)
        }
        views.toolbarPay.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
        views.optionPayment.setOnClickListener {
            PaymentTypeBottomSheet.getInstance().show(childFragmentManager, PaymentTypeBottomSheet.TAG)
        }
        views.layoutAddress.setOnClickListener {
            paymentViewModel.returnAddressFragment()
        }
        views.btnPayCash.setOnClickListener {
            payment(Status.STATUS_TIEN_MAT)
        }
        views.btnPayPaypal.setup(
            createOrder = CreateOrder { createOrderActions ->
                if (myCart != null && myAddress != null && myAddress?._id != null) {
                    val orderPaypal = com.paypal.checkout.order.OrderRequest(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList = listOf(
                            PurchaseUnit(
                                amount = Amount(
                                    currencyCode = CurrencyCode.USD,
                                    value = (((myCart?.total ?: 0.0) - (myCart?.totalCoupon ?: 0.0) + (myAddress?.deliveryFee ?: 0.0)) / 24000).formatPaypal()
                                ),
                            )
                        ),
                    )
                    paymentViewModel.returnShowLoading(true)
                    createOrderActions.create(orderPaypal)
                } else {
                    activity?.showUtilDialog(
                        Notify(
                            getString(R.string.pay),
                            getString(R.string.address_not_empty),
                            "Thieu thong tin nguoi dung",
                            R.raw.animation_falure
                        )
                    )
                }
            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Toast.makeText(requireContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show()
                    payment(Status.STATUS_PAYPAL, true)
                }
            },
            onCancel = OnCancel {
                Toast.makeText(requireContext(), "Bạn đã thoát thanh toán", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "OnCancel")
                paymentViewModel.returnShowLoading(false)
            },
            onError = OnError {
                Toast.makeText(requireContext(), "Thanh toán thất bại", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "OnError $it")
                paymentViewModel.returnShowLoading(false)
            }
        )
    }

    private fun payment(idStatus: String, isPayment: Boolean? = false) {
        if (myCart != null && myAddress != null && myAddress?._id != null) {
            val orderRequest = OrderRequest(
                myAddress?._id,
                strNote,
                null,
                idStatus,
                isPayment,
            )
            payNowViewModel.handle(PayNowViewAction.CreateOder(myCart, orderRequest))
        } else {
            activity?.showUtilDialog(
                Notify(
                    getString(R.string.pay),
                    getString(R.string.address_not_empty),
                    "Thieu thong tin nguoi dung",
                    R.raw.animation_falure
                )
            )
        }
    }

    private fun beforeFinish(result: OrderResponse) {
        val resultIntent = Intent()
        val resultBundle = Bundle()
        resultBundle.putSerializable("resultDataPaymentBundle", result)
        resultIntent.putExtra("resultDataPaymentIntent", resultBundle)
        activity?.apply {
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun setupButtonPayment(listPaymentType: ArrayList<Menu>, menu: Menu?){
        views.optionPayment.text = menu?.name ?: "Tiền mặt"

        views.btnPayCash.isVisible = false
        views.btnPayPaypal.isVisible = false

        when(menu?.id){
            listPaymentType[0].id -> views.btnPayCash.isVisible = true
            listPaymentType[1].id -> views.btnPayPaypal.isVisible = true
            listPaymentType[2].id -> views.btnPayCash.isVisible = true
            listPaymentType[3].id -> views.btnPayCash.isVisible = true
            listPaymentType[4].id -> views.btnPayCash.isVisible = true
            else ->{ views.btnPayCash.isVisible = true }
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPayBinding {
        return FragmentPayBinding.inflate(layoutInflater)
    }

    override fun invalidate() {
        views.swipeLoading.isRefreshing = withState(userViewModel){it.asyncListAddress is Loading} || withState(paymentViewModel){it.asyncCurentCart is Loading}

        withState(userViewModel) {
            when(it.asyncListAddress){
                is Success ->{
                    var listAddress = it.asyncListAddress.invoke()
                    if (listAddress != null){
                        myAddress = listAddress.find { it.isSelected }
                        setupLayoutAddress()
                    }else{
                        Toast.makeText(requireContext(), "Bạn chưa có địa chỉ hãy thêm địa chỉ", Toast.LENGTH_SHORT).show()
                    }
                }
                is Fail ->{
                    Toast.makeText(requireContext(), "Không có danh sách địa chỉ", Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }

        withState(payNowViewModel) {

            when (it.curentCart) {
                is Success -> {
                    myCart = it.curentCart.invoke()
                    setupUi()
                }

                is Fail -> {
                    Toast.makeText(requireContext(), "Không có giỏ hàng", Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                }

                else -> {
                }
            }

            when (it.asyncAddOrder) {
                is Success -> {
                    addOrder = it.asyncAddOrder.invoke()

                    beforeFinish(addOrder!!)
                    paymentViewModel.returnShowLoading(false)
                    it.asyncAddOrder = Uninitialized
                }

                is Fail -> {
                    Log.e("PayFragment", "invalidate: ${(it.asyncAddOrder as Fail<OrderResponse>).error.message!!.trim()}", )
                    activity?.showUtilDialog(
                        Notify(
                            getString(R.string.pay),
                            getString(R.string.payment_fail),
                            getString(R.string.error_500),
                            R.raw.animation_falure
                        )
                    )
                    paymentViewModel.returnShowLoading(false)
                    it.asyncAddOrder = Uninitialized
                }

                else -> {}
            }
        }

        withState(paymentViewModel) {
            paymentViewModel.returnShowLoading(it.asyncAddOrder is Loading || it.asyncUpdatePaymentOrder is Loading)
            setupButtonPayment(it.paymentTypies ,it.curentPaymentType)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0
    }

}