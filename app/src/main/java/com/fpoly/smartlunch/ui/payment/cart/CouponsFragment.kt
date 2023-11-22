package com.fpoly.smartlunch.ui.payment.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentCouponsBinding
import com.fpoly.smartlunch.ui.main.coupons.adapter.CouponsAdapter
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.payment.PaymentViewModel

class CouponsFragment : PolyBaseFragment<FragmentCouponsBinding>() {
    private val productViewModel: ProductViewModel by activityViewModel()
    private val paymentViewModel: PaymentViewModel by activityViewModel()
    private var adapterCoupons: CouponsAdapter? = null
    private var adapterCouponsRunningOutOfDate: CouponsAdapter? = null

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCouponsBinding {
        return FragmentCouponsBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        listenEvent()
    }

    private fun listenEvent() {
        views.layoutHeader.btnBackToolbar.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    private fun setupAppBar() {
        views.layoutHeader.apply {
            btnBackToolbar.visibility=View.VISIBLE
            tvTitleToolbar.text = "Khuyến mãi"
        }
    }

    private fun setupListCoupon() {
        adapterCoupons = CouponsAdapter {
            onClickItemCoupons(it)
        }
        adapterCouponsRunningOutOfDate = CouponsAdapter {
            onClickItemCoupons(it)
        }
        views.rcyCoupon.adapter = adapterCoupons
        views.rcyCouponRunningOutOfDate.adapter = adapterCouponsRunningOutOfDate
    }

    private fun onClickItemCoupons(id: String) {
        productViewModel.handle(ProductAction.GetDetailCoupons(id))
        paymentViewModel.returnCouponsDetailFragment()
    }

    override fun invalidate(): Unit = withState(productViewModel) {
        when (it.asyncCoupons) {
            is Success -> {
                setupListCoupon()
                adapterCoupons?.setData(it.asyncCoupons.invoke())
                adapterCouponsRunningOutOfDate?.setData(it.asyncCoupons.invoke())
            }

            else -> {}
        }
    }

}