package com.fpoly.smartlunch.ui.main.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.ChangeQuantityRequest
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.databinding.BottomsheetFragmentHomeBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCart
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import retrofit2.http.Query

class HomeBottomSheet : PolyBaseBottomSheet<BottomsheetFragmentHomeBinding>() {

    private val productViewModel: ProductViewModel by activityViewModel()

    private val userViewModel: UserViewModel by activityViewModel()


    private lateinit var idProduct : String
    private lateinit var sizeId : String
    private var purchaseQuantity : Int? = null
    private val homeViewModel: HomeViewModel by activityViewModel()




    private lateinit var adapterCart: AdapterCart
    // mặc định là như này
    override val isBorderRadiusTop: Boolean
        get() = true
    override val isDraggable: Boolean
        get() = true
    override val isExpanded: Boolean
        get() = false

    companion object{
        const val TAG = "HomeBottomSheet"
        fun newInstance() = HomeBottomSheet()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetFragmentHomeBinding = BottomsheetFragmentHomeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        listenEvent()
    }

    private fun listenEvent() {
        views.deleteCart.setOnClickListener {
            showClearCartConfirmationDialog()
        }
        views.vuesaxLineVisible.setOnClickListener{
            this.dismiss()
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {
                    val builder = AlertDialog.Builder(context)

                    builder.setTitle("Xác nhận xóa")
                    builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng không?")

                    builder.setPositiveButton("Xóa") { dialog, which ->
                        withState(userViewModel){
                            val userId = it.asyncCurrentUser.invoke()?._id
                            if (userId != null){
                                productViewModel.handle(ProductAction.getRemoveProductByIdCart(userId,idProduct,sizeId))

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
           homeViewModel.returnCartFragment()
            this.dismiss()
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

   private fun clearCart(){
       withState(userViewModel){
           val userId = it.asyncCurrentUser.invoke()?._id
           if (userId != null){
               productViewModel.handle(ProductAction.GetClearCart(userId))
           }

       }
   }

    private fun initUi() {
        withState(userViewModel){
            val userId = it.asyncCurrentUser.invoke()?._id
            if (userId != null){
                productViewModel.handle(ProductAction.GetOneCartById(userId))
            }
        }

        adapterCart = AdapterCart{ idProductAdapter , currentSoldQuantity ,currentSizeID ->
            sizeId = currentSizeID
            idProduct = idProductAdapter
            purchaseQuantity = currentSoldQuantity


        }
        views.rcvCart.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL , false)
        views.rcvCart.adapter = adapterCart
    }

    override fun onPause() {
        super.onPause()
        withState(userViewModel){
            val userId = it.asyncCurrentUser.invoke()?._id
            if (userId != null){
                if(purchaseQuantity != null){
                    productViewModel.handle(
                        ProductAction.GetChangeQuantity(userId,idProduct, ChangeQuantityRequest(
                            purchaseQuantity!!,
                            sizeId
                        )
                        ))
                }
            }
        }
    }
    override fun invalidate(): Unit = withState(productViewModel) {
        when (it.getOneCartById) {
            is Success -> {
                adapterCart.setData(it.getOneCartById.invoke()?.products)
                productViewModel.handleRemoveAsyncGetCart()
            }
            else -> {
            }
        }
        when(it.getClearCart){
            is Success -> {
                initUi()
                adapterCart.setData(it.getOneCartById.invoke()?.products)
                productViewModel.handleRemoveAsyncClearCart()
            }
            else -> {}
        }
        when(it.getRemoveProductByIdCart){
            is Success -> {
                initUi()
                adapterCart.setData(it.getOneCartById.invoke()?.products)
                productViewModel.handleRemoveAsyncProductCart()
            }
            else -> {}
        }


    }

}