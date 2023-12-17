package com.fpoly.smartlunch.ui.paynow.cart

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.CouponsRequest
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.SortPagingProduct
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.databinding.FragmentCartPayNowBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCart
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCoupons
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
import com.fpoly.smartlunch.ui.paynow.PayNowViewAction
import com.fpoly.smartlunch.ui.paynow.PayNowViewModel
import com.fpoly.smartlunch.ultis.formatCash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CartPayNowFragment : PolyBaseFragment<FragmentCartPayNowBinding>(){
    private var myDelayJob: Job? = null
    private var isSwipeLoading = false

    private val payNowViewModel: PayNowViewModel by activityViewModel()
    private val paymentViewModel: PaymentViewModel by activityViewModel()

    private lateinit var adapterCart: AdapterCart
    private lateinit var adapterCoupons: AdapterCoupons

    private var products: ArrayList<ProductCart> = arrayListOf()
    private var currentCartResponse: CartResponse? = null
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCartPayNowBinding {
        return FragmentCartPayNowBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        listenEvent()
    }
    private fun init(){
        views.apply {
            layoutToolbarCart.apply {
                btnBackToolbar.visibility = View.VISIBLE
                tvTitleToolbar.text = getString(R.string.cart)
            }
        }

        views.tvGiamGia.text = getString(R.string.min_cost)
        views.tvTam.text = (currentCartResponse?.total ?: 0.0).formatCash()
        views.tvTong.text = ((currentCartResponse?.total ?: 0.0) - (currentCartResponse?.totalCoupon ?: 0.0)).formatCash()

        adapterCoupons = AdapterCoupons {
            payNowViewModel.handle(PayNowViewAction.SetCupontCart(currentCartResponse, it))
        }

        adapterCart = AdapterCart(object : AdapterCart.ItemClickLisstenner(){
            override fun onClickItem(
                idProductAdapter: String,
            ) {
                super.onClickItem(idProductAdapter)
            }

            override fun onSwipeItem(
                idProductAdapter: String,
                currentSoldQuantity: Int?,
                currentSizeID: String
            ) {
                super.onSwipeItem(idProductAdapter, currentSoldQuantity, currentSizeID)
                paymentViewModel.handle(PaymentViewAction.GetRemoveProductByIdCart(idProductAdapter, currentSizeID))
            }

            override fun onChangeQuantity(
                idProductAdapter: String,
                currentSoldQuantity: Int,
                currentSizeID: String,
                toppingId: String?
            ) {
                super.onChangeQuantity(idProductAdapter, currentSoldQuantity, currentSizeID, toppingId)
                myDelayJob?.cancel()
                myDelayJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    payNowViewModel.handle(
                        PayNowViewAction.ChangeQuantityProduct(currentCartResponse, idProductAdapter, ChangeQuantityRequest(currentSoldQuantity, currentSizeID, toppingId))
                    )
                }
            }
        })

        views.rcCart.adapter = adapterCart
        views.recyclerViewVoucher.adapter = adapterCoupons
    }

    private fun listenEvent() {
        views.layoutToolbarCart.btnBackToolbar.setOnClickListener {
            activity?.finish()
        }
        views.swipeLoading.setOnRefreshListener {
            isSwipeLoading = true
            payNowViewModel.handle(PayNowViewAction.CheckUpdateCartLocal(currentCartResponse))
            paymentViewModel.handle(PaymentViewAction.GetListCoupons)
            paymentViewModel.handle(PaymentViewAction.GetOneCartById)
        }
        views.moreCoupon.setOnClickListener {
//            paymentViewModel.returnCouponsFragment()
        }
        views.btnTiepTuc.setOnClickListener {
            sendDataToPayScreen()
        }
    }

    private fun sendDataToPayScreen() {
        if (currentCartResponse != null && currentCartResponse!!.total > 0){
            val bundle = Bundle()
            bundle.putString("strNote", views.note.text.toString().ifEmpty { getString(R.string.note_default)})
            payNowViewModel.returnPayFragment(bundle)
        }else{
            Toast.makeText(requireContext(), getString(R.string.insufficient_cart), Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetCostInCard() {
        views.tvTam.text = (currentCartResponse?.total ?: 0.0).formatCash()
        views.tvGiamGia.text = (currentCartResponse?.totalCoupon ?: 0.0).formatCash()
        views.tvTong.text = ((currentCartResponse?.total ?: 0.0) - (currentCartResponse?.totalCoupon ?: 0.0)).formatCash()

        adapterCoupons.selectItem(currentCartResponse?.couponId)
    }


    override fun invalidate() {
        withState(payNowViewModel){
            when (it.curentCart) {
                is Success -> {
                    currentCartResponse = it.curentCart.invoke()
                    products = currentCartResponse?.products!!
                    adapterCart.setData(products)

                    resetCostInCard()
                    isSwipeLoading = false
                }
                is Fail ->{
                    isSwipeLoading = false
                }
                else -> {}
            }
        }

        withState(paymentViewModel) {
            if (isSwipeLoading)views.swipeLoading.isRefreshing = it.asyncCurentCart is Loading
            else views.swipeLoading.isRefreshing = false

            when (it.asyncCoupons) {
                is Success -> {
                    adapterCoupons.setData(it.asyncCoupons.invoke())
                    resetCostInCard()
                }
                else -> {}
            }

            if(it.catchError?.isNotEmpty() == true){
                Toast.makeText(requireContext(), it.catchError, Toast.LENGTH_SHORT).show()
                it.catchError = null
            }
        }
    }
}
