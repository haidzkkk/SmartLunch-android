package com.fpoly.smartlunch.ui.main.coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.databinding.FragmentCouponsDetailBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel

class CouponsDetailFragment : PolyBaseFragment<FragmentCouponsDetailBinding>(){
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        listenEvent()
    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupAppBar() {
    views.appBar.apply {
        btnBackToolbar.visibility=View.VISIBLE
        tvTitleToolbar.text="Chi tiết mã giảm giá"
    }
    }

    private fun setupUI(currentCoupons: CouponsResponse) {
    views.apply {
       Glide.with(requireContext()).load(currentCoupons.coupon_image)
           .placeholder(R.drawable.loading_img)
           .error(R.drawable.loading_img)
           .into(imgCoupons)
        couponName.text=currentCoupons.coupon_name
        couponCode.text=currentCoupons.coupon_code
        couponDate.text=currentCoupons.expiration_date
        couponDesc.text=currentCoupons.coupon_content
    }
        views.btnApply.setOnClickListener {
            productViewModel.handle(ProductAction.ApplyCoupon(CouponsRequest(currentCoupons._id)))
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        homeViewModel.returnVisibleBottomNav(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.returnVisibleBottomNav(true)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCouponsDetailBinding {
       return FragmentCouponsDetailBinding.inflate(inflater,container,false)
    }

    override fun invalidate():Unit= withState(productViewModel) {
        when(it.asyncOneCoupons){
            is Success -> {
                it.asyncOneCoupons.invoke()?.let { currentCoupons -> setupUI(currentCoupons) }
            }

            else -> {

            }
        }
    }

}