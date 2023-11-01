package com.fpoly.smartlunch.ui.main.product

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.databinding.FragmentFoodDetailBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterSize
import com.fpoly.smartlunch.ui.main.profile.UserViewModel

class ProductFragment : PolyBaseFragment<FragmentFoodDetailBinding>() {

    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private var adapterSize: AdapterSize? = null
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
            productViewModel.handle(ProductAction.GetSizeById(idSize))
        }
        views.rcvSize.adapter = adapterSize
    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
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
            Glide.with(requireContext()).load(currentProduct?.images?.get(0)?.url)
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
                image = it.images[0].url,
                product_name = it.product_name,
                product_price = it.product_price,
                purchase_quantity = currentSoldQuantity!!,
                sizeId = sizeId!!
            )
        }
        withState(userViewModel) {
            val userId = it.asyncCurrentUser.invoke()?._id
            if (userId != null) {
                productViewModel.handle(ProductAction.CreateCart(userId, newCartProduct!!))
            }
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
        homeViewModel.returnVisibleBottomNav(false)
    }

    override fun getBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentFoodDetailBinding {
        return FragmentFoodDetailBinding.inflate(layoutInflater)
    }

    override fun invalidate(): Unit = withState(productViewModel) {
        when (it.asynGetAllSize) {
            is Success -> {
                it.asynGetAllSize.invoke()?.let {
                    adapterSize?.setData(it)
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
                productViewModel.handleRemoveAsyncGetFavourite()
            }

            is Fail -> {
                disableLike()
            }

            else -> {}
        }
    }

}