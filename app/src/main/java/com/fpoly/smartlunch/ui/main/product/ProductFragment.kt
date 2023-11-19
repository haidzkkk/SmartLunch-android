package com.fpoly.smartlunch.ui.main.product

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
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
import com.fpoly.smartlunch.ui.main.home.adapter.BannerAdapter
import com.fpoly.smartlunch.ui.main.home.adapter.ImageSlideAdapter
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import kotlinx.coroutines.Job

class ProductFragment : PolyBaseFragment<FragmentFoodDetailBinding>() {

    private val productViewModel: ProductViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private lateinit var adapterSize: AdapterSize
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var imageSlideAdapter: ImageSlideAdapter

    private var currentSizeId: String? = null
    private var currentProduct: Product? = null
    private var currentSoldQuantity: Int? = 1
    private var sizeId: String? = null
    private var isLiked: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        initUi()
        listenEvent()
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        productViewModel.returnVisibleBottomNav(false)
    }
    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

    private fun initUi() {
        adapterSize = AdapterSize { idSize ->
            currentSizeId = idSize
            productViewModel.handle(ProductAction.GetSizeById(idSize))
        }

        imageSlideAdapter = ImageSlideAdapter{

        }
        commentAdapter = CommentAdapter()

        views.viewpagerImg.adapter = imageSlideAdapter
        views.rcvSize.adapter = adapterSize
        views.rcvComment.adapter = commentAdapter
        views.rcvComment.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        views.viewpagerImg.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                views.tvPositionImg.text = "${position + 1}/${(views.viewpagerImg.adapter as RecyclerView.Adapter).itemCount}"
            }
        })
    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetListCommentsLimit(withState(productViewModel){it.asyncProduct.invoke()?._id ?: ""}))
            productViewModel.handle(ProductAction.GetDetailProduct(withState(productViewModel){it.asyncProduct.invoke()?._id ?: ""}))
            productViewModel.handle(ProductAction.GetListSizeProduct(withState(productViewModel){it.asyncProduct.invoke()?._id ?: ""} ))
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
            productViewModel.returnCommentFragment()
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
    private fun initUiProduct(product: Product) {
        currentProduct = product
        views.apply {
            imageSlideAdapter.setData(product.images)
            NameDetailFood.text = currentProduct?.product_name
            priceDetailFood.text = "${currentProduct?.product_price.toString()} đ"
            descFood.text = currentProduct?.description
            someIdQuality.text = "1"
            cvPositionImg.isVisible = product.images.size > 1
            views.tvPositionImg.text = "1/${product.images.size}"
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

    override fun getBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentFoodDetailBinding {
        return FragmentFoodDetailBinding.inflate(layoutInflater)
    }

    override fun invalidate(): Unit = withState(productViewModel) {
        views.swipeLoading.isRefreshing = it.asyncTopProduct is Loading || it.asynGetSizeProduct is Loading

        when (it.asynGetSizeProduct) {
            is Success -> {
                it.asynGetSizeProduct.invoke()?.let {
                    adapterSize.setData(it)
                }

                it.asynGetSizeProduct = Uninitialized
            }

            else -> {}
        }
        when (it.asyncGetOneSize) {
            is Success -> {
                it.asyncGetOneSize.invoke()?.let {
                    sizeId = it._id
                    views.buttonAddCart.isEnabled = true
                }

                it.asyncGetOneSize = Uninitialized
            }

            else -> {}
        }
        when (it.asyncProduct) {
            is Success -> {
                it.asyncProduct.invoke()?.let { product ->
                    initUiProduct(product)
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
    }

}