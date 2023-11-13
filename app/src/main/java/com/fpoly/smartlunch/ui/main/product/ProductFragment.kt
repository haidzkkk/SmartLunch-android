package com.fpoly.smartlunch.ui.main.product

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.core.PolyDialog
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.Comment
import com.fpoly.smartlunch.data.model.CommentRequest
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.databinding.DialogAddCommentBinding
import com.fpoly.smartlunch.databinding.FragmentFoodDetailBinding
import com.fpoly.smartlunch.ui.chat.room.GalleryBottomSheetFragment
import com.fpoly.smartlunch.ui.main.comment.CommentAdapter
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterSize
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import kotlinx.coroutines.Job

class ProductFragment : PolyBaseFragment<FragmentFoodDetailBinding>() {

    private val productViewModel: ProductViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private lateinit var adapterSize: AdapterSize
    private lateinit var commentAdapter: CommentAdapter

    private var currentSizeId: String? = null
    private var currentProduct: Product? = null
    private var currentSoldQuantity: Int? = 1
    private var sizeId: String? = null
    private var isLiked: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        initUiSize()
        listenEvent()
    }

    private fun setupAppBar() {
        views.apply {
            appBar.apply {
                btnBackToolbar.apply {
                    setBackgroundResource(R.drawable.background_icon)
                    setImageResource(R.drawable.icon_white_arow_left)
                    visibility = View.VISIBLE
                }
                tvTitleToolbar.text = ""
            }
            buttonAddCart.isEnabled = false
        }
    }

    private fun initUiSize() {
        adapterSize = AdapterSize { idSize ->
            currentSizeId = idSize
            productViewModel.handle(ProductAction.GetSizeById(idSize))
        }
        views.rcvSize.adapter = adapterSize

        commentAdapter = CommentAdapter()
        views.rcvComment.adapter = commentAdapter
        views.rcvComment.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetListCommentsLimit(withState(productViewModel){it.asyncProduct.invoke()?._id ?: ""}))
            productViewModel.handle(ProductAction.GetDetailProduct(withState(productViewModel){it.asyncProduct.invoke()?._id ?: ""}))
            productViewModel.handle(ProductAction.GetListSize)
        }
        views.linearMinu2.setOnClickListener {
            increaseQuantity()
        }

        views.linearMinu1.setOnClickListener {
            reduceQuantity()
        }
        views.buttonAddCart.setOnClickListener {
            addCart()
        }
        views.tvSeeAllComment2.setOnClickListener{
            addTamCommentDialog()
        }
        views.tvSeeAllComment.setOnClickListener{
            productViewModel.returnCommentFragment()
        }

        views.btnLike.setOnClickListener {
            productViewModel.handle(ProductAction.LikeProduct(currentProduct!!))
            if (isLiked == false) {
                isLiked = true
                enableAnimation(views.animLike, R.raw.anim_like)
            } else {
                disableLike()
            }
        }
        views.animLike.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                views.btnLike.setImageResource(R.drawable.like_full)
                views.animLike.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
        views.animAddProduct.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                views.animAddProduct.visibility = View.GONE
                activity?.supportFragmentManager?.popBackStack()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
    }
    private fun initUi(product: Product) {
        currentProduct = product
        views.apply {
            Glide.with(requireContext()).load(if(currentProduct?.images?.isNotEmpty() == true) currentProduct?.images!![0].url else "")
                .placeholder(R.drawable.loading_img).error(R.drawable.loading_img)
                .into(imageFoodDetail)
            NameDetailFood.text = currentProduct?.product_name
            priceDetailFood.text = "${currentProduct?.product_price.toString()} đ"
            descFood.text = currentProduct?.description
            someIdQuality.text = "1"
        }
    }

    private fun addCart() {
        val newCartProduct = currentProduct?.let {
            CartRequest(
                productId = it._id,
                image = if(it.images.isNotEmpty()) it.images[0].url else "",
                product_name = it.product_name,
                product_price = it.product_price,
                purchase_quantity = currentSoldQuantity!!,
                sizeId = sizeId!!
            )
        }
        withState(userViewModel) {
            productViewModel.handle(ProductAction.CreateCart(newCartProduct!!))
        }
        currentSoldQuantity = 1
        enableAnimation(views.animAddProduct, R.raw.anim_add_to_cart)
    }

    private fun enableAnimation(view: LottieAnimationView, raw: Int) {
        view.visibility = View.VISIBLE
        view.setAnimation(raw)
        view.playAnimation()
    }

    private fun increaseQuantity() {
        currentSoldQuantity = currentSoldQuantity!! + 1
        views.someIdQuality.text = currentSoldQuantity.toString()
    }

    private fun reduceQuantity() {
        if (currentSoldQuantity!! > 1) {
            currentSoldQuantity = currentSoldQuantity!! - 1
            views.someIdQuality.text = currentSoldQuantity.toString()
        }
    }

    private fun enableLike() {
        isLiked = true
        views.btnLike.setImageResource(R.drawable.like_full)
    }

    private fun disableLike() {
        isLiked = false
        views.btnLike.setImageResource(R.drawable.like_emty)
    }

    override fun onResume() {
        super.onResume()
        productViewModel.returnVisibleBottomNav(false)
    }

    override fun getBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentFoodDetailBinding {
        return FragmentFoodDetailBinding.inflate(layoutInflater)
    }

    override fun invalidate(): Unit = withState(productViewModel) {
        views.swipeLoading.isRefreshing = it.asyncTopProduct is Loading || it.asynGetAllSize is Loading

        when (it.asynGetAllSize) {
            is Success -> {
                it.asynGetAllSize.invoke()?.let {
                    adapterSize.setData(it)
                }
            }

            else -> {}
        }
        when (it.asyncGetOneSize) {
            is Success -> {
                it.asyncGetOneSize.invoke()?.let {
                    sizeId = it._id
                    views.buttonAddCart.isEnabled = true
                }
            }

            else -> {}
        }
        when (it.asyncProduct) {
            is Success -> {
                it.asyncProduct.invoke()?.let { product ->
                    initUi(product)
                }
            }

            else -> {}
        }
        when (it.asyncGetFavourite) {
            is Success -> {
                enableLike()
                it.asyncGetFavourite = Uninitialized
            }

            is Fail -> {
                disableLike()
            }

            else -> {}
        }

        when(it.asyncCommentsLimit){
            is Success ->{
                commentAdapter.setData(it.asyncCommentsLimit.invoke())
//                it.asyncCommentsLimit = Uninitialized
            }
            else ->{

            }
        }

        when(it.asyncAddComment){
            is Success ->{
                it.asyncAddComment = Uninitialized
                Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show()
            }
            is Fail ->{
                it.asyncAddComment = Uninitialized
                Toast.makeText(requireContext(), "Có thể thiếu size hoặc đã comment", Toast.LENGTH_SHORT).show()
            }
            else ->{

            }
        }
    }
    private fun addTamCommentDialog() {
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
            var commentAdd = CommentRequest(currentProduct?._id, currentSizeId, "653768e1e6054414ddfd3fca",
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