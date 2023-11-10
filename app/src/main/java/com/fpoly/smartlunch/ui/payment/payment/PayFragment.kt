package com.fpoly.smartlunch.ui.payment.payment

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
import com.fpoly.smartlunch.ui.main.profile.AddressFragment
import com.fpoly.smartlunch.ui.main.profile.UserViewAction
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewState
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
import com.fpoly.smartlunch.ultis.Status
import com.fpoly.smartlunch.ultis.formatCash
import com.fpoly.smartlunch.ultis.showUtilDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CreateOrderActions
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.PurchaseUnit
import okhttp3.internal.filterList
import javax.inject.Inject

class PayFragment : PolyBaseFragment<FragmentPayBinding>(), OnMapReadyCallback {

    var mGoogleMap: GoogleMap? = null

    private val paymentViewModel: PaymentViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private var myCart: CartResponse? = null
    private var myAddress: Address? = null
    private var strNote: String = ""

    var addOrder: OrderResponse? = null
//    lateinit var orderRequest: OrderRequest

    private var myCreateOrderActions: CreateOrderActions? = null

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

    @SuppressLint("SetTextI18n")
    private fun setupUi() {
        views.apply {
            extraCost.text = getString(R.string.min_cost)
            discoutCost.text = (myCart?.totalCoupon ?: 0.0).formatCash()
            shipCost.text = getString(R.string.min_cost)
            couponCode.text = myCart?.couponId?._id
            total.text = myCart?.total?.formatCash()
        }
    }

    private fun setupMap() {
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_view_payment) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
    }

    private fun listenEvent() {
        views.swipeLoading.setOnRefreshListener {
            userViewModel.handle(UserViewAction.GetListAddress)
            paymentViewModel.handle(PaymentViewAction.GetOneCartById)
        }
        views.toolbarPay.btnBackToolbar.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
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
                this.myCreateOrderActions = createOrderActions
                payment(Status.STATUS_PAYPAL)
            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Toast.makeText(requireContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show()
                    paymentViewModel.handle(PaymentViewAction.UpdateIsPaymentOder(addOrder?._id!!, true))
                    // update isPayment của order thành true
                }
            },
            onCancel = OnCancel {
                Toast.makeText(requireContext(), "Bạn đã thoát thanh toán", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "OnCancel")
            },
            onError = OnError {
                Toast.makeText(requireContext(), "Thanh toán thất bại", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "OnError")
            }
        )
    }

    private fun payment(idStatus: String) {
        if (myCart != null && myAddress != null && myAddress?._id != null) {
            val orderRequest = OrderRequest(
                myAddress?._id!!,
                myAddress?.recipientName ?: "Tên người nhận",
                strNote,
                idStatus,
                false,
                myCart!!.products,
                myCart!!.total - (myCart!!.totalCoupon ?: 0.0),
                myCart!!.totalCoupon ?: 0.0,
            )
            paymentViewModel.handle(PaymentViewAction.CreateOder(orderRequest!!))
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

        withState(paymentViewModel) {
            paymentViewModel.returnShowLoading(it.asyncAddOrder is Loading || it.asyncUpdatePaymentOrder is Loading)
            setupButtonPayment(it.paymentTypies ,it.curentPaymentType)

            when(it.asyncCurentCart){
                is Success ->{
                    myCart = it.asyncCurentCart.invoke()
                    setupUi()
                }
                is Fail ->{
                    Toast.makeText(requireContext(), "Không có giỏ hàng", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                else ->{
                }
            }

            when (it.asyncUpdatePaymentOrder){
                is Success -> {
                    beforeFinish(it.asyncUpdatePaymentOrder.invoke()!!)
                }
                is Fail ->{
                    Log.e("TAG", "invalidate: ${(it.asyncUpdatePaymentOrder as Fail<OrderResponse>).error.message}", )
                }
                else ->{}
            }

            when (it.asyncAddOrder) {
                is Success -> {
                    addOrder = it.asyncAddOrder.invoke()

                    // thanh toán paypal
                    if (myCreateOrderActions != null && addOrder != null){
                        val orderPaypal = com.paypal.checkout.order.OrderRequest(
                            intent = OrderIntent.CAPTURE,
                            appContext = AppContext(userAction = UserAction.PAY_NOW),
                            purchaseUnitList = listOf(
                                PurchaseUnit(
                                    amount = Amount(
                                        currencyCode = CurrencyCode.USD,
                                        value = (addOrder?.total.toString().toInt() / 24).toString()
                                    ),
                                )
                            ),
                        )
                        paymentViewModel.returnShowLoading(true)
                        myCreateOrderActions!!.create(orderPaypal)
                    }else{
                        beforeFinish(addOrder!!)
                    }
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
                }

                else -> {}
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0
    }

}