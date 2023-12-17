package com.fpoly.smartlunch.ui.payment.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.databinding.BottomSheetPaymentTypeBinding
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ui.payment.PaymentViewModel

class PaymentTypeBottomSheet() : PolyBaseBottomSheet<BottomSheetPaymentTypeBinding>(){

    val paymentViewModel: PaymentViewModel by activityViewModel()

    lateinit var adapter: PaymentTypeAdapter

    companion object{
        const val TAG = "PaymentTypeBottomSheet"
        fun getInstance() = PaymentTypeBottomSheet()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetPaymentTypeBinding = BottomSheetPaymentTypeBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
        adapter = PaymentTypeAdapter {
            paymentViewModel.handle(PaymentViewAction.setCurrentPaymentType(it))
            dismiss()
        }

        views.layoutHeader.tvTitleToolbar.text = getString(R.string.pay)

        views.rcv.adapter = adapter
        views.rcv.layoutManager = LinearLayoutManager(requireContext())
        views.rcv.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    override fun invalidate() {
        withState(paymentViewModel){
            adapter.setData(it.paymentTypies)
            adapter.setSelectItem(it.curentPaymentType)
        }
    }
}