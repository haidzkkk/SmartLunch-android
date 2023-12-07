package com.fpoly.smartlunch.ui.main.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.core.PolyDialog
import com.fpoly.smartlunch.data.model.CommentRequest
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.OrderResponse
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.ProductOrder
import com.fpoly.smartlunch.databinding.DialogAddCommentBinding
import com.fpoly.smartlunch.databinding.FragmentProductReviewBinding
import com.fpoly.smartlunch.databinding.ItemProductReviewBinding
import com.fpoly.smartlunch.ui.chat.room.GalleryBottomSheetFragment
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel

class ProductReviewFragment: PolyBaseFragment<FragmentProductReviewBinding>(){

    lateinit var adapter: ProductReviewAdapter
    var currentOrderResponse: OrderResponse? = null

    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProductReviewBinding {
        return FragmentProductReviewBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        listenEvent()
    }
    private fun initUI() {
        adapter = ProductReviewAdapter(
            {
                productViewModel.handle(ProductAction.GetDetailProduct(it.productId))
                productViewModel.handle(ProductAction.GetListSizeProduct(it.productId))
                productViewModel.handle(ProductAction.GetListToppingProduct(it.productId))
                productViewModel.handle(ProductAction.GetListCommentsLimit(it.productId))
                homeViewModel.returnDetailProductFragment()
            },
            {
                if (currentOrderResponse != null) {
                    addCommentDialog(currentOrderResponse!!, it)
                }else{
                    Toast.makeText(requireContext(), "Không có order", Toast.LENGTH_SHORT).show()
                }
            }
        )
        views.rcvProductReview.adapter = adapter

        views.layoutHeader.tvTitleToolbar.text = "Đánh giá sản phẩm"
        views.layoutHeader.btnBackToolbar.isVisible = true
    }

    private fun listenEvent() {
        views.layoutHeader.btnBackToolbar.setOnClickListener{
            activity?.onBackPressed()
        }
    }


    override fun invalidate() {
        withState(productViewModel){
            when(it.addOrder){
                is Success -> {
                    adapter.setData(it.addOrder.invoke())
                    currentOrderResponse = it.addOrder.invoke()
                }
                is Fail -> {
                }
                else -> {}
            }

            when(it.asyncAddComment){
                is Success ->{
                    it.asyncAddComment = Uninitialized
                    Toast.makeText(requireContext(), "Đánh giá thành công", Toast.LENGTH_SHORT).show()
                }
                is Fail ->{
                    it.asyncAddComment = Uninitialized
                    Toast.makeText(requireContext(), "Đánh giá thất bại", Toast.LENGTH_SHORT).show()
                }
                else ->{

                }
            }
        }
    }

    private fun addCommentDialog(orderResponse: OrderResponse, productOrder: ProductOrder) {
        val dialog = PolyDialog
            .Builder(requireContext(), DialogAddCommentBinding.inflate(layoutInflater))
            .build()
        dialog.show()

        val bindingDialog = dialog.binding
        var rate = 5
        var listImage: ArrayList<Gallery>? = null

        bindingDialog.tvGalarey.setOnClickListener{
            var bottomGallery = GalleryBottomSheetFragment {
                listImage = it
                bindingDialog.tvGalarey.text = it.size.toString()
            }
            bottomGallery.show(requireActivity().supportFragmentManager, "TAG")
        }

        bindingDialog.btnSend.setOnClickListener{
            val commentAdd = CommentRequest(productOrder.productId, productOrder.sizeId, orderResponse._id,
                bindingDialog.edtDesc.text.toString(), rate)
            productViewModel.handle(ProductAction.AddComment(commentAdd, listImage))
            dialog.dismiss()
        }

        bindingDialog.start1.setOnClickListener{
            rate = 1
            handleClickStartComment(bindingDialog, 1)
        }

        bindingDialog.start2.setOnClickListener{
            rate = 2
            handleClickStartComment(bindingDialog, 2)
        }

        bindingDialog.start3.setOnClickListener{
            rate = 3
            handleClickStartComment(bindingDialog, 3)
        }

        bindingDialog.start4.setOnClickListener{
            rate = 4
            handleClickStartComment(bindingDialog, 4)
        }

        bindingDialog.start5.setOnClickListener{
            rate = 5
            handleClickStartComment(bindingDialog, 5)
        }
    }

    fun handleClickStartComment(binding: DialogAddCommentBinding, index: Int){
        binding.start1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        binding.start2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        binding.start3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        binding.start4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        binding.start5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)

        when (index){
            1 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            2 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            3 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            4 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
            5 ->{
                binding.start1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.start5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }
    }

}