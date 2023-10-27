package com.fpoly.smartlunch.ui.main.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.databinding.FragmentPayBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import javax.inject.Inject


class PayFragment @Inject constructor() : PolyBaseFragment<FragmentPayBinding>() {
    private val homeViewModel: HomeViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()
    var userId :String?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orderRequest = arguments?.getSerializable("order_request") as OrderRequest
        setupUi(orderRequest)
    }
    private fun setupUi(orderRequest: OrderRequest) {
        withState(userViewModel) {
            userId = it.asyncCurrentUser.invoke()?._id
            if (userId != null) {
                views.tvSdt.text = it.asyncCurrentUser.invoke()?.phone
            }
        }
        views.apply {
            toolbarPay.apply {
                btnBackToolbar.visibility = View.VISIBLE
                tvTitleToolbar.text = getString(R.string.cart)
                btnBackToolbar.setOnClickListener{
                    homeViewModel.returnCartFragment()
                }
            }
        }

        Toast.makeText(requireContext(), ""+orderRequest.userId, Toast.LENGTH_SHORT).show()
        views.btnThanhToan.setOnClickListener{
            productViewModel.handle(ProductAction.CreateOder(orderRequest))
            productViewModel.handle(ProductAction.GetClearCart(userId!!))
        }

    }
    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPayBinding {
        return FragmentPayBinding.inflate(layoutInflater)
    }

    override fun invalidate():Unit= withState(productViewModel) {

    }
}