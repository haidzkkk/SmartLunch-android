package com.fpoly.smartlunch.ui.main.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.OrderRequest
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.databinding.FragmentCartBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCart
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCoupons
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import javax.inject.Inject

class CartFragment @Inject constructor() : PolyBaseFragment<FragmentCartBinding>() {
    private val paymentViewModel: PaymentViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private var adapter: AdapterProduct? = null
    private var adapterCart: AdapterCart? = null
    private var adapterCoupons: AdapterCoupons? = null

    private var products: List<ProductCart> = listOf()
    private var currentCartResponse: CartResponse? = null
    private var userId: String? = null
    private var total: Int = 0
    private var discount: Int =0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        init()
        listenEvent()
    }

    private fun setupAppBar() {
        views.apply {
            layoutToolbarCart.apply {
                btnBackToolbar.visibility = View.VISIBLE
                tvTitleToolbar.text = getString(R.string.cart)
            }
        }
    }

    private fun init(): Unit = withState(paymentViewModel) {
        userId = withState(userViewModel) {
            it.asyncCurrentUser.invoke()?._id
        }
        if (userId != null) {
            paymentViewModel.handle(PaymentViewAction.GetOneCartById(userId!!))
        }
    }

    private fun listenEvent() {
        views.layoutToolbarCart.btnBackToolbar.setOnClickListener {
            activity?.finish()
        }
        views.btnThem.setOnClickListener {
            activity?.finish()
        }
        views.btnTiepTuc.setOnClickListener {
            sendDataToPayScreen()
        }
    }

    private fun sendDataToPayScreen() {
        val orderRequest = OrderRequest(
            "",
            "",
            getInputData(),
            getString(R.string.payment_id),
            getString(R.string.payment_id),
            getString(R.string.payment_id),
            "",
            products,
            currentCartResponse!!.total,
            discount,
            userId!!
        )
        val bundle = Bundle()
        bundle.putSerializable("order_request", orderRequest)
        paymentViewModel.returnPayFragment(bundle)
    }

    private fun getInputData():String {
        var note: String = views.note.text.toString()
        if (note.isNullOrEmpty()){
            note= getString(R.string.note_default)
        }
        return note
    }

    private fun setupPaymentInfo() {
        total = currentCartResponse?.total!!
        views.tvGiamGia.text = getString(R.string.min_cost)
        views.tvPhi.text = getString(R.string.min_cost)
        views.tvTam.text = currentCartResponse?.total.toString()
        views.tvTong.text = currentCartResponse?.total.toString()
    }

    private fun setupListProductInCard() {
        adapterCart = AdapterCart(
            { _, _, _ -> },
            { _, _, _ ->
            })
        views.rcCart.adapter = adapterCart
    }

    private fun setupListProduct() {
        adapter = AdapterProduct {
        }
        views.recyclerViewProductPb.adapter = adapter
    }

    private fun setupListCoupons() {
        adapterCoupons = AdapterCoupons {
            paymentViewModel.handle(
                PaymentViewAction.ApplyCoupon(
                    userId!!,
                    CouponsRequest(it)
                )
            )
        }
        views.recyclerViewVoucher.adapter = adapterCoupons
    }

    private fun resetCostInCard() {
        discount = (total - currentCartResponse?.total!!)
        views.tvGiamGia.text =(total - currentCartResponse?.total!!).toString()
        views.tvTong.text = currentCartResponse?.total.toString()
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCartBinding {
        return FragmentCartBinding.inflate(layoutInflater)
    }

    override fun invalidate(): Unit = withState(paymentViewModel) {
        when (it.asyncProducts) {
            is Success -> {
                setupListProduct()
                adapter?.setData(it.asyncProducts.invoke()?.docs)
            }

            else -> {}
        }
        when (it.asyncGetOneCartById) {
            is Success -> {
                currentCartResponse = it.asyncGetOneCartById.invoke()
                products = it.asyncGetOneCartById.invoke()?.products!!
                setupListProductInCard()
                adapterCart?.setData(products)
                setupPaymentInfo()
            }

            else -> {}
        }

        when (it.asyncCoupons) {
            is Success -> {
                setupListCoupons()
                adapterCoupons?.setData(it.asyncCoupons.invoke())
            }

            else -> {}
        }
        when (it.asyncApplyCoupons) {
            is Success -> {
                currentCartResponse = it.asyncApplyCoupons.invoke()
                resetCostInCard()
            }

            is Fail -> {
                views.tvGiamGia.text = getString(R.string.min_cost)
            }

            else -> {
            }
        }

    }

}


