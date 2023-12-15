package com.fpoly.smartlunch.ui.main.coupons

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
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel

class CouponsFragment : PolyBaseFragment<FragmentCouponsBinding>() {
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private var adapterCoupons: CouponsAdapter? = null

    companion object {
        const val TAG = "CardFragment"
    }
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCouponsBinding {
        return FragmentCouponsBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
    }

    private fun setupAppBar() {
        views.layoutHeader.tvTitleToolbar.text = "Khuyến mãi"
        productViewModel.handle(ProductAction.GetListCoupons)
    }

    private fun setupListCoupon() {
        adapterCoupons = CouponsAdapter {
            onClickItemCoupons(it)
        }
        views.rcyCoupon.adapter = adapterCoupons
    }

    private fun onClickItemCoupons(id: String) {
        productViewModel.handle(ProductAction.GetDetailCoupons(id))
        homeViewModel.returnCouponsDetailFragment()
    }

    override fun invalidate(): Unit = withState(productViewModel) {
        when (it.asyncCoupons) {
            is Success -> {
                setupListCoupon()
                adapterCoupons?.setData(it.asyncCoupons.invoke())
            }

            else -> {}
        }
    }

}