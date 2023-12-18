package com.fpoly.smartlunch.ui.payment.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
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
import com.fpoly.smartlunch.data.model.OrderZaloPayRequest
import com.fpoly.smartlunch.data.model.ZaloPayInfo
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentPayBinding
import com.fpoly.smartlunch.ui.main.profile.UserViewAction
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewState
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
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
import com.paypal.checkout.createorder.CreateOrderActions
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.PurchaseUnit
import okhttp3.internal.filterList
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import javax.inject.Inject


class PayFragment : PolyBaseFragment<FragmentPayBinding>(), OnMapReadyCallback {

    var mGoogleMap: GoogleMap? = null

    private val paymentViewModel: PaymentViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private var myCart: CartResponse? = null
    private var myAddress: Address? = null
    private var strNote: String = ""
    private var data: String = ""

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
            recipientName.text = myAddress?.recipientName
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
            shipCost.text = (myAddress?.deliveryFee ?: 0.0).toInt().formatCash()
            couponCode.text = myCart?.couponId?._id
            total.text = ((myCart?.total ?: 0.0) - (myCart?.totalCoupon ?: 0.0)).formatCash()
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
                                    value = (((myCart?.total ?: 0.0) - (myCart?.totalCoupon ?: 0.0)) / 24).toInt().toString()
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
                            getString(R.string.lack_of_info),
                            R.raw.animation_falure
                        )
                    )
                }
            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture {
                    Toast.makeText(requireContext(), getString(R.string.payment_success), Toast.LENGTH_SHORT).show()
                    payment(Status.STATUS_PAYPAL, true)
                }
            },
            onCancel = OnCancel {
                Toast.makeText(requireContext(), getString(R.string.payment_cancel), Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "OnCancel")
                paymentViewModel.returnShowLoading(false)
            },
            onError = OnError {
                Toast.makeText(requireContext(), getString(R.string.payment_failure), Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "OnError $it")
                paymentViewModel.returnShowLoading(false)
            }
        )

        views.btnPayZalopay.setOnClickListener{
            paymentViewModel.returnShowLoading(true)

            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            ZaloPaySDK.init(ZaloPayInfo.APP_ID, Environment.SANDBOX)

            val amount = ((myCart?.total ?: 0.0) - (myCart?.totalCoupon ?: 0.0) + (myAddress?.deliveryFee ?: 0.0)).formatPaypal()
            var orderZalopay = OrderZaloPayRequest.createOrder(amount){
                data = it
            }
            paymentViewModel.handle(PaymentViewAction.CreateOrderZaloPay(orderZalopay))
        }
    }

    private fun payment(idStatus: String, isPayment: Boolean? = false) {
        if (myCart != null && myAddress != null && myAddress?._id != null) {
            val orderRequest = OrderRequest(
                myAddress?._id,
                strNote,
                null,
                idStatus,
                isPayment,
                data
            )
            paymentViewModel.handle(PaymentViewAction.CreateOder(orderRequest))
        } else {
            activity?.showUtilDialog(
                Notify(
                    getString(R.string.pay),
                    getString(R.string.address_not_empty),
                    getString(R.string.missing_user_info),
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
        data = ""
        views.optionPayment.text = menu?.name

        views.btnPayCash.isVisible = false
        views.btnPayPaypal.isVisible = false
        views.btnPayZalopay.isVisible = false

        when(menu?.id){
            listPaymentType[0].id -> views.btnPayCash.isVisible = true
            listPaymentType[1].id -> views.btnPayPaypal.isVisible = true
            listPaymentType[2].id -> views.btnPayZalopay.isVisible = true
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
                        Toast.makeText(requireContext(), getString(R.string.add_address_prompt), Toast.LENGTH_SHORT).show()
                    }
                }
                is Fail ->{
                    Toast.makeText(requireContext(), getString(R.string.no_address), Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }

        withState(paymentViewModel) {
            paymentViewModel.returnShowLoading(it.asyncAddOrder is Loading)

            if (it.curentPaymentType != null){
                setupButtonPayment(it.paymentTypies, it.curentPaymentType)
                it.curentPaymentType = null
            }

            when(it.asyncCurentCart){
                is Success ->{
                    myCart = it.asyncCurentCart.invoke()
                    setupUi()
                }
                is Fail ->{
                    Toast.makeText(requireContext(), getString(R.string.no_cart), Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                }
                else ->{
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
                    Log.e("PayFragment", "invalidate: ${(it.asyncAddOrder as Fail<OrderResponse>).error.message!!.trim()}")
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

                else -> {
                }
            }

            when(it.asyncOrderZaloPayReponse){
                is Success -> {
                    val data = it.asyncOrderZaloPayReponse.invoke()
                    if (data?.return_code.equals("1")){
                        val token = data?.zp_trans_token
                        handlePaymentZaloPay(token.toString())
                    }else{
                        Toast.makeText(requireContext(), getString(R.string.create_zalopay_order_fail), Toast.LENGTH_SHORT).show()
                    }

                    it.asyncOrderZaloPayReponse = Uninitialized
                }
                is Fail ->{
                    Toast.makeText(requireContext(), getString(R.string.create_zalopay_order_fail), Toast.LENGTH_SHORT).show()
                    it.asyncOrderZaloPayReponse = Uninitialized
                }
                else ->{
                }
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0
    }

    private fun handlePaymentZaloPay(token: String){
        ZaloPaySDK.getInstance().payOrder(requireActivity(), token, "demozpdkpayment://app", object : PayOrderListener{
            override fun onPaymentSucceeded(p0: String?, p1: String?, p2: String?) {
                Toast.makeText(requireContext(), getString(R.string.payment_success), Toast.LENGTH_SHORT).show()
                payment(Status.STATUS_ZALOPAY, true)
            }

            override fun onPaymentCanceled(p0: String?, p1: String?) {
                Toast.makeText(requireContext(), getString(R.string.payment_cancel), Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "OnCancel")
                paymentViewModel.returnShowLoading(false)
            }

            override fun onPaymentError(p0: ZaloPayError?, p1: String?, p2: String?) {
                Toast.makeText(requireContext(), getString(R.string.payment_failure), Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "OnError $p0")
                paymentViewModel.returnShowLoading(false)
            }

        })
    }

}