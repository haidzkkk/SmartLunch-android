package com.fpoly.smartlunch.ui.main.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.BottomSheetPaymentTypeBinding
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.payment.payment.PaymentTypeAdapter
import com.fpoly.smartlunch.ultis.getRateComment

class CommentRateBottom: PolyBaseBottomSheet<BottomSheetPaymentTypeBinding>() {

    private val productViewModel: ProductViewModel by activityViewModel()

    private var currentProduct: Product? = null
    lateinit var adapter: PaymentTypeAdapter

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetPaymentTypeBinding = BottomSheetPaymentTypeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        views.layoutHeader.tvTitleToolbar.text = getString(R.string.filter)

        adapter = PaymentTypeAdapter {
            productViewModel.handle(ProductAction.GetListComments(currentProduct?._id ?: "", rate = it.id.toInt()))
            dismiss()
        }
        views.rcv.adapter = adapter
        views.rcv.layoutManager = LinearLayoutManager(requireContext())
        views.rcv.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        adapter.setData(getRateComment())
    }

    override fun invalidate() {
        withState(productViewModel){
            when (it.asyncProduct) {
                is Success -> {
                    it.asyncProduct.invoke()?.let { product ->
                        currentProduct = product
                    }
                }

                else -> {}
            }
        }
    }
}