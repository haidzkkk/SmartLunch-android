package com.fpoly.smartlunch.ui.payment.cart

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
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
import com.fpoly.smartlunch.databinding.FragmentCartBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCart
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCoupons
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.payment.PaymentViewAction
import com.fpoly.smartlunch.ui.payment.PaymentViewModel
import com.fpoly.smartlunch.ultis.formatCash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("SetTextI18n")
class CartFragment @Inject constructor() : PolyBaseFragment<FragmentCartBinding>() {
    private var myDelayJob: Job? = null
    private var isSwipeLoading = false

    private val productViewModel: ProductViewModel by activityViewModel()
    private val paymentViewModel: PaymentViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private lateinit var adapter: AdapterProduct
    private lateinit var adapterCart: AdapterCart
    private lateinit var adapterCoupons: AdapterCoupons

    private var products: ArrayList<ProductCart> = arrayListOf()
    private var currentCartResponse: CartResponse? = null
    private var currentUser: User? = null

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

    private fun init(){
        paymentViewModel.handle(PaymentViewAction.GetOneCartById)

        views.tvGiamGia.text = getString(R.string.min_cost)
        views.tvTam.text = (currentCartResponse?.total ?: 0.0).formatCash()
        views.tvTong.text = ((currentCartResponse?.total ?: 0.0) - (currentCartResponse?.totalCoupon ?: 0.0)).formatCash()

        adapter = AdapterProduct(object: AdapterProduct.OnClickListenner{
            override fun onCLickItem(id: String) {
//                productViewModel.handle(ProductAction.GetListSizeProduct(id))
//                productViewModel.handle(ProductAction.GetListToppingProduct(id))
//                productViewModel.handle(ProductAction.GetListCommentsLimit(id))
//                productViewModel.handle(ProductAction.GetDetailProduct(id))
//                paymentViewModel.returnDetailProductFragment()
            }

            override fun onCLickSeeMore() {
                var bundle = Bundle().apply {
                    putString("sort", SortPagingProduct.bought)
                }
                paymentViewModel.returnSearchProductFragment(bundle)
            }

        })

        adapterCoupons = AdapterCoupons {
            paymentViewModel.handle(
                PaymentViewAction.ApplyCoupon(CouponsRequest(it._id))
            )
        }

        adapterCart = AdapterCart(object : AdapterCart.ItemClickLisstenner(){
            override fun onClickItem(
                idProductAdapter: String,
            ) {
                super.onClickItem(idProductAdapter)
//                productViewModel.handle(ProductAction.GetListSizeProduct(idProductAdapter))
//                productViewModel.handle(ProductAction.GetListToppingProduct(idProductAdapter))
//                productViewModel.handle(ProductAction.GetListCommentsLimit(idProductAdapter))
//                productViewModel.handle(ProductAction.GetDetailProduct(idProductAdapter))
//                paymentViewModel.returnDetailProductFragment()
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
                    paymentViewModel.handle(
                        PaymentViewAction.GetChangeQuantity(idProductAdapter, ChangeQuantityRequest(currentSoldQuantity, currentSizeID, toppingId))
                    )
                }
            }
        })

        views.rcCart.adapter = adapterCart
        views.recyclerViewVoucher.adapter = adapterCoupons
        views.recyclerViewProductPb.adapter = adapter
    }

    private fun listenEvent() {
        views.layoutToolbarCart.btnBackToolbar.setOnClickListener {
            activity?.finish()
        }
        views.swipeLoading.setOnRefreshListener {
            isSwipeLoading = true
            paymentViewModel.handle(PaymentViewAction.GetListCoupons)
            paymentViewModel.handle(PaymentViewAction.GetOneCartById)
        }
        views.btnThem.setOnClickListener {
            paymentViewModel.returnSearchProductFragment()
        }
        views.moreCoupon.setOnClickListener {
            paymentViewModel.returnCouponsFragment()
        }
        views.btnTiepTuc.setOnClickListener {
            paymentViewModel.handle(PaymentViewAction.GetOneCartById)
            sendDataToPayScreen()
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.LEFT) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Xác nhận xóa")
                    builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng không?")
                    builder.setPositiveButton("Xóa") { dialog, which ->
                        adapterCart.onItemSwiped(viewHolder.bindingAdapterPosition)
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("Hủy") { dialog, which ->
                        adapterCart.notifyItemChanged(viewHolder.bindingAdapterPosition)
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }).attachToRecyclerView(views.rcCart)
    }

    private fun sendDataToPayScreen() {
        if (currentCartResponse != null && currentCartResponse!!.total > 0){
            val bundle = Bundle()
            bundle.putString("strNote", views.note.text.toString().ifEmpty { getString(R.string.note_default)})
            paymentViewModel.returnPayFragment(bundle)
        }else{
            Toast.makeText(requireContext(), "Giỏ hàng không đủ điều kiện để thanh toán", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetCostInCard() {
        views.tvTam.text = (currentCartResponse?.total ?: 0.0).formatCash()
        views.tvGiamGia.text = (currentCartResponse?.totalCoupon ?: 0.0).formatCash()
        views.tvTong.text = ((currentCartResponse?.total ?: 0.0) - (currentCartResponse?.totalCoupon ?: 0.0)).formatCash()

        adapterCoupons.selectItem(currentCartResponse?.couponId)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCartBinding {
        return FragmentCartBinding.inflate(inflater, container, false)
    }

    override fun invalidate() {
        withState(userViewModel){
            when(it.asyncCurrentUser){
                is Success -> {
                    currentUser = it.asyncCurrentUser.invoke()
                }
                else ->{
                }
            }
        }

        withState(paymentViewModel) {
            if (isSwipeLoading)views.swipeLoading.isRefreshing = it.asyncCurentCart is Loading
            else views.swipeLoading.isRefreshing = false

            when (it.asyncProducts) {
                is Success -> {
                    adapter.setData(it.asyncProducts.invoke()?.docs)
                }
                else -> {}
            }

            when (it.asyncCurentCart) {
                is Success -> {
                    currentCartResponse = it.asyncCurentCart.invoke()
                    products = currentCartResponse?.products!!
                    adapterCart.setData(products)

                    resetCostInCard()
//                    it.asyncCurentCart = Uninitialized

                    isSwipeLoading = false
                }
                is Fail ->{
                    isSwipeLoading = false
                }
                else -> {}
            }

            when (it.asyncCoupons) {
                is Success -> {
                    adapterCoupons.setData(it.asyncCoupons.invoke())
                    resetCostInCard()

//                    it.asyncCoupons = Uninitialized
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


