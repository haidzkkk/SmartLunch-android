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
import com.fpoly.smartlunch.ultis.StringUltis
import com.fpoly.smartlunch.ultis.convertIsoToStringFormat
import com.fpoly.smartlunch.ultis.formatCash

class CouponsDetailFragment : PolyBaseFragment<FragmentCouponsDetailBinding>(){
    private val productViewModel: ProductViewModel by activityViewModel()

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
        tvTitleToolbar.text = getString(R.string.coupon_detail)
    }
    }

    private fun setupUI(currentCoupons: CouponsResponse) {
    views.apply {

        Glide.with(root.context).load(if(currentCoupons.coupon_images.isNotEmpty()) currentCoupons.coupon_images[0].url else "")
            .placeholder(R.drawable.loading_img)
            .error(R.drawable.loading_img)
            .into(imgCoupons)
        tvName.text= currentCoupons.coupon_name
        tvCode.text= "${currentCoupons.discount_amount}%"
        tvAmount.text= "${getString(R.string.minimum)} ${currentCoupons.min_purchase_amount?.toDouble()?.formatCash()}"
        tvDate.text= currentCoupons.expiration_date?.convertIsoToStringFormat(StringUltis.dateDayFormat)
        tvDesc.text= currentCoupons.coupon_content
    }
        views.btnApply.setOnClickListener {
            productViewModel.handle(ProductAction.ApplyCoupon(CouponsRequest(currentCoupons._id)))
        }
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