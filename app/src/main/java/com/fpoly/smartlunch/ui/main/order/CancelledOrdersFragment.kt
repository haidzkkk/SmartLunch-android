package com.fpoly.smartlunch.ui.main.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.OrderZaloPayRequest
import com.fpoly.smartlunch.databinding.FragmentCancelledOrdersBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
import com.fpoly.smartlunch.ultis.Status

class CancelledOrdersFragment : PolyBaseFragment<FragmentCancelledOrdersBinding>(){

    private val paymentViewModel: PaymentViewModel by activityViewModel()
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCancelledOrdersBinding {
        return FragmentCancelledOrdersBinding.inflate(inflater,container,false)
    }

    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
    }

    private fun handleDataRespone(list: List<OrderResponse>?){
        val adapter = OrderAdapter{id->
            productViewModel.handle(ProductAction.GetCurrentOrder(id))
            homeViewModel.returnOrderDetailFragment()
        }
        views.rcyOrder.adapter = adapter
        adapter.setData(list)

        if (list != null){
            list.forEach {currentOrder ->
                if(currentOrder.status._id == Status.CANCEL_STATUS && currentOrder.isPayment && !currentOrder.data.isNullOrEmpty() &&
                    (currentOrder.statusPayment._id == Status.STATUS_ZALOPAY || currentOrder.statusPayment._id == Status.STATUS_PAYPAL)
                ){
                    paymentViewModel.handle(
                        PaymentViewAction.StatusOrderZaloPay(
                            OrderZaloPayRequest.queryStatusOrderZalo(currentOrder.data)))
                    paymentViewModel.handle(PaymentViewAction.UpdateIsPaymentOder(currentOrder._id ?: "", false))
                }
            }
        }
    }

    override fun invalidate() {
        withState(productViewModel){
            when(it.asyncCancelled){
                is Success -> {
                    handleDataRespone(it.asyncCancelled.invoke())
                    it.asyncCancelled = Uninitialized
                }

                else -> {}
            }
        }

        withState(paymentViewModel){
            when(it.asyncStatusOrderZaloPayReponse){
                is Success ->{
                    val data = it.asyncStatusOrderZaloPayReponse.invoke()
                    if (data?.amount != null && data.zp_trans_id != null && it.asyncRefundOrderZaloPayReponse == Uninitialized){
                        paymentViewModel.handle(PaymentViewAction.RefundOrderZaloPay(OrderZaloPayRequest.refundOrderZalo(data.zp_trans_id, data.amount)))
                    }
                    it.asyncStatusOrderZaloPayReponse = Uninitialized
                }
                else ->{

                }
            }
            when(it.asyncRefundOrderZaloPayReponse){
                is Success ->{
                    it.asyncRefundOrderZaloPayReponse = Uninitialized
                }
                is Fail ->{
                    Toast.makeText(requireContext(), getString(R.string.refund_failed), Toast.LENGTH_SHORT).show()
                    it.asyncRefundOrderZaloPayReponse = Uninitialized
                }
                else ->{

                }
            }
        }
    }

}