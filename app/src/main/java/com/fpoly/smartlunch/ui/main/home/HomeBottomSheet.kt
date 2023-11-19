package com.fpoly.smartlunch.ui.main.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.databinding.BottomsheetFragmentHomeBinding
import com.fpoly.smartlunch.databinding.LayoutBottomCartBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCart
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.payment.PaymentActivity
import com.fpoly.smartlunch.ui.payment.payment.PayFragment
import com.fpoly.smartlunch.ultis.formatCash
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeBottomSheet : PolyBaseBottomSheet<BottomsheetFragmentHomeBinding>() {
    lateinit var btnCommit: Button
    var tvQuantity: TextView? = null

    private var myDelayJob: Job? = null

    var cartResponse: CartResponse? = null

    private val productViewModel: ProductViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()

    private var idProduct: String? = null
    private var sizeId: String? = null
    private var purchaseQuantity: Int? = null

    private lateinit var adapterCart: AdapterCart
    override val isBorderRadiusTop: Boolean
        get() = true
    override val isDraggable: Boolean
        get() = true
    override val isExpanded: Boolean
        get() = false

    companion object {
        const val TAG = "HomeBottomSheet"
        fun newInstance() = HomeBottomSheet()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        listenEvent()
    }

    private fun listenEvent() {
        views.deleteCart.setOnClickListener {
            showClearCartConfirmationDialog()
        }
        views.vuesaxLineVisible.setOnClickListener {
            this.dismiss()
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
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.LEFT) {
                    val builder = AlertDialog.Builder(context)
                    adapterCart.onItemSwiped(viewHolder.bindingAdapterPosition)
                    builder.setTitle("Xác nhận xóa")
                    builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng không?")

                    builder.setPositiveButton("Xóa") { dialog, which ->
                        withState(userViewModel) {
                            val userId = it.asyncCurrentUser.invoke()?._id
                            if (idProduct != null && sizeId != null) {
                                productViewModel.handle(ProductAction.GetRemoveProductByIdCart(idProduct!!, sizeId!!))
                            }
                        }
                        dialog.dismiss()
                    }

                    builder.setNegativeButton("Hủy") { dialog, which ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }).attachToRecyclerView(views.rcvCart)


//        btnCommit.setOnClickListener {
//            activity?.startActivityForResult(
//                Intent(requireContext(), PaymentActivity::class.java),
//                PayFragment.ACTIVITY_PAY_REQUEST_CODE
//            )
//            dismiss()
//        }

    }

    private fun showClearCartConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear Cart")
            .setMessage("Are you sure you want to clear your cart?")
            .setPositiveButton("Yes") { dialog, _ ->
                clearCart()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun clearCart() {
        withState(userViewModel) {
            productViewModel.handle(ProductAction.GetClearCart)
        }
    }

    private fun initUi() {
        productViewModel.handle(ProductAction.GetOneCartById)
        adapterCart = AdapterCart(object : AdapterCart.ItemClickLisstenner(){
            override fun onSwipeItem(idProductAdapter: String, currentSoldQuantity: Int?, currentSizeID: String) {
                sizeId = currentSizeID
                idProduct = idProductAdapter
                purchaseQuantity = currentSoldQuantity
            }

            override fun onChangeQuantity(
                idProductAdapter: String,
                currentSoldQuantity: Int,
                currentSizeID: String
            ) {
                super.onChangeQuantity(idProductAdapter, currentSoldQuantity, currentSizeID)

                myDelayJob?.cancel() // Hủy bỏ công việc trước nếu có
                myDelayJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500) // Chờ đợi một khoảng thời gian
                    // Sau khi chờ đợi, thực hiện cuộc gọi với trạng thái cuối cùng
                    productViewModel.handle(
                        ProductAction.GetChangeQuantity(idProductAdapter, ChangeQuantityRequest(currentSoldQuantity, currentSizeID))
                    )
                }
            }
        })
        views.rcvCart.adapter = adapterCart
    }
    fun updateDataUI(){
        adapterCart.setData(cartResponse?.products)
        tvQuantity?.text = cartResponse?.products?.size.toString()
        btnCommit.text = "Thanh toán ${cartResponse?.total?.formatCash()}"
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetFragmentHomeBinding = BottomsheetFragmentHomeBinding.inflate(layoutInflater)

    override fun invalidate(): Unit = withState(productViewModel) {

        when (it.curentCartResponse) {
            is Success -> {
                cartResponse =  it.curentCartResponse.invoke()
                if (cartResponse != null) updateDataUI()
                else Toast.makeText(requireContext(), "getOneCartById Không có dữ liệu", Toast.LENGTH_SHORT).show()
            }

            is Fail ->{
                Toast.makeText(requireContext(), "getOneCartById Lỗi", Toast.LENGTH_SHORT).show()
            }
            else -> {
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener {
            val coordinator = (it as BottomSheetDialog).findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
            val containerLayout = it.findViewById<FrameLayout>(com.google.android.material.R.id.container)

            var layoutButtoms = bottomSheetDialog.layoutInflater.inflate(R.layout.layout_bottom_cart, null)

            tvQuantity = layoutButtoms.findViewById(R.id.tv_quantity)
            btnCommit = layoutButtoms.findViewById(R.id.btn_commit)

            btnCommit.setOnClickListener {
                activity?.startActivityForResult(
                    Intent(requireContext(), PaymentActivity::class.java),
                    PayFragment.ACTIVITY_PAY_REQUEST_CODE
                )
                dismiss()
            }

            layoutButtoms.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
            containerLayout!!.addView(layoutButtoms)

            layoutButtoms.post {
                (coordinator!!.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    layoutButtoms.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    this.bottomMargin = layoutButtoms.measuredHeight
                    containerLayout.requestLayout()
                }
            }
        }
        return bottomSheetDialog
    }

}