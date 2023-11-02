package com.fpoly.smartlunch.ui.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Address
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentPayBinding
import com.fpoly.smartlunch.ui.main.profile.UserViewAction
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ultis.showUtilDialog
import javax.inject.Inject

class PayFragment : PolyBaseFragment<FragmentPayBinding>() {
    private val paymentViewModel: PaymentViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private var myCart: CartResponse? = null
    private var myAddress: Address? = null
    private var orderRequest: OrderRequest? = null

    @Inject
    lateinit var sessionManager: SessionManager

    companion object {
        const val ACTIVITY_PAY_REQUEST_CODE = 123
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        orderRequest = arguments?.getSerializable("order_request") as OrderRequest
        setupAppBar()
        init()
        setupUi()
        listenEvent()
    }

    private fun setupAppBar() {
        views.toolbarPay.apply {
            btnBackToolbar.visibility = View.VISIBLE
            tvTitleToolbar.text = getString(R.string.pay)
        }
    }

    private fun init() {
        myCart = withState(paymentViewModel) {
            it.asyncApplyCoupons.invoke() ?: it.asyncGetOneCartById.invoke()
        }
        withState(userViewModel) {
            myAddress = it.asyncAddress.invoke()
            setupLayoutAddress()
        }
    }

    private fun setupLayoutAddress(){
        views.apply {
            address.text=myAddress?.addressLine
            phone.text=myAddress?.phoneNumber
        }
    }

    private fun setupUi() {
        views.apply {
            extraCost.text = getString(R.string.min_cost)
            discoutCost.text = "${orderRequest?.discount.toString()}đ"
            shipCost.text = getString(R.string.min_cost)
            couponCode.text = myCart?.couponId.toString()
            total.text = myCart?.total.toString()
        }
    }

    private fun listenEvent() {
        views.toolbarPay.btnBackToolbar.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        views.optionPayment.setOnClickListener {

        }
        views.layoutAddress.setOnClickListener {
            paymentViewModel.returnAddressFragment()
        }
        views.btnPay.setOnClickListener {
            orderRequest?.let { payment(it) }
        }
    }

    private fun payment(orderRequest: OrderRequest) {
        if (myAddress != null) {
            orderRequest.apply {
                address = myAddress?.addressLine.toString()
                phone = myAddress?.phoneNumber.toString()
                consignee_name = myAddress?.recipientName.toString()
            }
            paymentViewModel.handle(PaymentViewAction.CreateOder(orderRequest))
        } else {
            activity?.showUtilDialog(
                Notify(
                    getString(R.string.pay),
                    getString(R.string.address_not_empty),
                    "vui lòng chọn địa chỉ giao hàng",
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

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPayBinding {
        return FragmentPayBinding.inflate(layoutInflater)
    }

    override fun invalidate(): Unit = withState(paymentViewModel) {
        when (it.asyncAddOrder) {
            is Success -> {
                val result: OrderResponse = it.asyncAddOrder.invoke()!!
                beforeFinish(result)
            }

            is Fail -> {
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