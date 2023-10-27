package com.fpoly.smartlunch.ui.main.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.CouponsResponse
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.databinding.FragmentCartBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCart
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCoupons
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProductVer
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import javax.inject.Inject


class CartFragment @Inject constructor() : PolyBaseFragment<FragmentCartBinding>() {
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()
    var adapter: AdapterProduct? = null
    var adapterCart: AdapterCart? = null
    var products : List<ProductCart>? = listOf()
    var currentCartResponse: CartResponse? = null
    var userId: String?= null
    var adapterCoupons: AdapterCoupons? = null
    var note :String? = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productViewModel.handle(ProductAction.GetListProduct)
        productViewModel.handle(ProductAction.GetListCoupons)
        initUi()
        listenEvent()
    }

    private fun listenEvent() {
        views.btnThem.setOnClickListener {
            homeViewModel.returnHomeFragment()
        }
        views.btnTiepTuc.setOnClickListener {
            val bundle = Bundle()
            val orderRequest = OrderRequest(
                "HN",
                "Khuyen",
                note!!,
                "aa",
                "aa",
                "aa",
                "0988833333",
                products!!,
                currentCartResponse!!.total,
                userId!!
            )
            bundle.putSerializable("order_request",orderRequest)
            homeViewModel.returnPayFragment(bundle)
        }
    }

    private fun initUi(): Unit = withState(productViewModel) {
        userId = withState(userViewModel) {
            it.asyncCurrentUser.invoke()?._id
        }

        if (userId != null) {
            productViewModel.handle(ProductAction.GetOneCartById(userId!!))
        }
        currentCartResponse=it.getOneCartById.invoke()
        products=it.getOneCartById.invoke()?.products
        //setup toolbar
        views.apply {
            layoutToolbarCart.apply {
                btnBackToolbar.visibility = View.VISIBLE
                tvTitleToolbar.text = getString(R.string.cart)
                btnBackToolbar.setOnClickListener {
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }

        //productCart
        adapterCart = AdapterCart {
            productViewModel.handle(ProductAction.oneProduct(it))
            homeViewModel.returnDetailProductFragment()
        }
        adapterCart?.setData(products)
        views.rcCart.adapter = adapterCart
        views.rcCart.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.tvTam.text = it.getOneCartById.invoke()?.total.toString()
        views.tvTong.text = it.getOneCartById.invoke()?.total.toString()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCartBinding {
        return FragmentCartBinding.inflate(layoutInflater)
    }

    override fun invalidate(): Unit = withState(productViewModel) {
        when (it.products) {
            is Loading -> Log.e("TAG", "HomeFragment view state: Loading")
            is Success -> {
                //setup product
                adapter = AdapterProduct {
                    productViewModel.handle(ProductAction.oneProduct(it))
                    homeViewModel.returnDetailProductFragment()
                }
                adapter?.setData(it.products.invoke()?.docs)
                views.recyclerViewProductPb.adapter = adapter
                views.recyclerViewProductPb.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            }

            else -> {}
        }
        when (it.getOneCartById) {
            is Loading -> Log.e("TAG", "HomeFragment view state: Loading")
            is Success -> {

            }

            else -> {}
        }

        when (it.coupons) {
            is Loading -> Log.e("TAG", "HomeFragment view state: Loading")
            is Success -> {
                adapterCoupons = AdapterCoupons {
                    productViewModel.handle(ProductAction.applyCoupon(userId!!,CouponsRequest(it)))
                }
                adapterCoupons?.setData(it.coupons.invoke())
                views.recyclerViewVoucher.adapter = adapterCoupons
                views.recyclerViewVoucher.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }

            else -> {}
        }
        when(it.applyCoupons){
            is Success -> {
                currentCartResponse=it.applyCoupons.invoke()
            }

            else -> {}
        }

    }

}


