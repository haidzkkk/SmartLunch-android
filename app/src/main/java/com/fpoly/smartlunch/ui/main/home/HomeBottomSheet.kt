package com.fpoly.smartlunch.ui.main.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.databinding.BottomsheetFragmentHomeBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCart
import com.fpoly.smartlunch.ui.payment.payment.PayFragment
import com.fpoly.smartlunch.ui.payment.PaymentActivity
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel

class HomeBottomSheet : PolyBaseBottomSheet<BottomsheetFragmentHomeBinding>() {

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


        views.buttonThanh.setOnClickListener {
            activity?.startActivityForResult(
                Intent(requireContext(), PaymentActivity::class.java),
                PayFragment.ACTIVITY_PAY_REQUEST_CODE
            )
            dismiss()
        }

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
                productViewModel.handle(
                    ProductAction.GetChangeQuantity(idProductAdapter, ChangeQuantityRequest(currentSoldQuantity, currentSizeID))
                )
            }
        })
        views.rcvCart.adapter = adapterCart
    }
    fun updateDataUI(cartResponse: CartResponse){
        adapterCart.setData(cartResponse.products)
        views.quantityProduct.text = cartResponse.products.size.toString()
        views.buttonThanh.text = "Thanh toán ${cartResponse.total} VND"
    }

    override fun onPause() {
        super.onPause()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetFragmentHomeBinding = BottomsheetFragmentHomeBinding.inflate(layoutInflater)

    override fun invalidate(): Unit = withState(productViewModel) {
        when (it.getOneCartById) {
            is Success -> {
                val cartGetOne =  it.getOneCartById.invoke()
                if (cartGetOne != null) updateDataUI(cartGetOne)
                else Toast.makeText(requireContext(), "getOneCartById Không có dữ liệu", Toast.LENGTH_SHORT).show()

                it.getOneCartById = Uninitialized
            }

            is Fail ->{
                Toast.makeText(requireContext(), "getOneCartById Lỗi", Toast.LENGTH_SHORT).show()
            }

            else -> {
                it.getOneCartById = Uninitialized
            }
        }
        when (it.getClearCart) {
            is Success -> {
                adapterCart.setData(it.getOneCartById.invoke()?.products)
                productViewModel.handleRemoveAsyncClearCart()
                productViewModel.handleUpdateCart()

                it.getClearCart = Uninitialized
            }

            else -> {
                it.getClearCart = Uninitialized
            }
        }
        when (it.getRemoveProductByIdCart) {
            is Success -> {
                adapterCart.setData(it.getOneCartById.invoke()?.products)
                productViewModel.handleRemoveAsyncProductCart()
                productViewModel.handleUpdateCart()
                it.getRemoveProductByIdCart = Uninitialized
            }

            else -> {
                it.getRemoveProductByIdCart = Uninitialized
            }
        }
        when (it.getChangeQuantity) {

            is Success -> {
                var cartChangeQuantity = it.getChangeQuantity.invoke()
                if (cartChangeQuantity != null) updateDataUI(cartChangeQuantity)
                else Toast.makeText(requireContext(), "getOneCartById Không có dữ liệu", Toast.LENGTH_SHORT).show()

                it.getChangeQuantity = Uninitialized
            }

            is Fail -> {
                Toast.makeText(requireContext(), "Không thay đổi được số lượng", Toast.LENGTH_SHORT).show()
                it.getOneCartById = Uninitialized
            }

            else -> {}
        }
    }


}