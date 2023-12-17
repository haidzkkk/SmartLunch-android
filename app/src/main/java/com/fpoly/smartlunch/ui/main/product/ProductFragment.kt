package com.fpoly.smartlunch.ui.main.product

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
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
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.CartResponse
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductCart
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.data.model.Topping
import com.fpoly.smartlunch.data.model.ToppingCart
import com.fpoly.smartlunch.data.network.RemoteDataSource
import com.fpoly.smartlunch.databinding.FragmentFoodDetailBinding
import com.fpoly.smartlunch.ui.main.comment.CommentAdapter
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterSize
import com.fpoly.smartlunch.ui.main.home.adapter.ImageSlideAdapter
import com.fpoly.smartlunch.ui.main.home.adapter.ToppingAdapter
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ui.payment.PaymentActivity
import com.fpoly.smartlunch.ui.payment.payment.PayFragment
import com.fpoly.smartlunch.ui.paynow.PayNowActivity
import com.fpoly.smartlunch.ultis.StringUltis
import com.fpoly.smartlunch.ultis.convertDateToStringFormat
import com.fpoly.smartlunch.ultis.formatCash
import com.fpoly.smartlunch.ultis.formatRate
import com.fpoly.smartlunch.ultis.formatView
import java.util.Date

@SuppressLint("SetTextI18n")
class ProductFragment : PolyBaseFragment<FragmentFoodDetailBinding>() {

    private val productViewModel: ProductViewModel by activityViewModel()

    private lateinit var adapterSize: AdapterSize
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var imageSlideAdapter: ImageSlideAdapter
    private lateinit var toppingAdapter: ToppingAdapter

    private var currentSize: Size? = null
    private var currentProduct: Product? = null
    private var isLiked: Boolean = false
    private var currentSoldQuantity: Int? = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        initUi()
        listenEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        withState(productViewModel){
            it.asyncComments = Uninitialized
            it.asyncProduct = Uninitialized
            it.asynGetSizeProduct = Uninitialized
        }
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
            currentSize = idSize
            views.buttonAddCart.isEnabled = true
            views.priceDetailFood.text = (currentSize!!.size_price).formatCash()
        }
        adapterSize.setSelect(currentSize)
        views.rcvSize.adapter = adapterSize

        toppingAdapter = ToppingAdapter(ToppingAdapter.TYPE_ITEM_MEDIUM, object : ToppingAdapter.OnItenClickLisstenner{
            override fun onItemClick(topping: Topping) {

            }

            override fun onChangeQuantity(topping: Topping) {

            }
        })
        views.rcvTopping.adapter = toppingAdapter

        imageSlideAdapter = ImageSlideAdapter{

        }
        commentAdapter = CommentAdapter()

        views.viewpagerImg.adapter = imageSlideAdapter
        views.rcvComment.adapter = commentAdapter
        views.rcvComment.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        views.viewpagerImg.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                views.tvPositionImg.text = "${position + 1}/${(views.viewpagerImg.adapter as RecyclerView.Adapter).itemCount}"
            }
        })

        initUiProduct()
    }

    private fun listenEvent() {
        views.appBar.btnBackToolbar.setOnClickListener {
            activity?.onBackPressed()
        }
        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetListComments(currentProduct?._id ?: "", limit = 2))
            productViewModel.handle(ProductAction.GetDetailProduct(currentProduct?._id ?: ""))
            productViewModel.handle(ProductAction.GetListSizeProduct(currentProduct?._id ?: ""))
            productViewModel.handle(ProductAction.GetListToppingProduct(currentProduct?._id ?: ""))
        }
        views.linearMinu2.setOnClickListener {
            if (currentProduct != null){
                increaseQuantity()
            }
        }

        views.linearMinu1.setOnClickListener {
            if (currentProduct != null){
                reduceQuantity()
            }
        }
        views.tvSeeAllComment.setOnClickListener{
            if (currentProduct != null){
                productViewModel.returnCommentFragment()
            }
        }
        views.tvSeeMore.setOnClickListener{
            if (currentProduct != null){
                handleStateDesc()
            }
        }
        views.tvDesc.setOnClickListener{
            handleStateDesc()
        }
        views.btnShare.setOnClickListener{
            if (currentProduct != null){
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, "${RemoteDataSource.BASE_URL}/api/admin/products/${currentProduct?._id}")
                requireActivity().startActivity(Intent.createChooser(shareIntent, "Chia sẻ URL qua"))
            }
        }

        views.btnLike.setOnClickListener {
            if (currentProduct != null){
                isLiked = !isLiked
                if (isLiked){
                    views.btnLike.setImageResource(R.drawable.like_full)
                }
                else{
                    views.btnLike.setImageResource(R.drawable.like_emty)
                }
                productViewModel.handle(ProductAction.LikeProduct(currentProduct!!))
            }
        }

        views.buttonAddCart.setOnClickListener {
            if (currentProduct != null){
                addCart()
            }
        }
        views.buttonPay.setOnClickListener{
            if (currentProduct != null){
                sendCart()
            }
        }
        views.animAddProduct.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                views.animAddProduct.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
    }

    private fun handleStateDesc() {
        if (views.tvDesc.maxLines == 2){
            views.tvDesc.maxLines = 1000
            views.tvSeeMore.text = "Ẩn bớt"
        }else{
            views.tvDesc.maxLines = 2
            views.tvSeeMore.text = "Xem thêm"
        }
    }

    private fun initUiProduct() {
        views.apply {
            imageSlideAdapter.setData(currentProduct?.images)
            NameDetailFood.text = currentProduct?.product_name
            priceDetailFood.text = currentProduct?.product_price?.formatCash()
            tvDesc.text = "${currentProduct?.description} \n\n${currentProduct?.views?.formatView()} lượt xem"
            someIdQuality.text = "1"
            tvBought.text = "Đã bán ${currentProduct?.bought?.formatView()}"
            tvTitleCommemt.text = "${currentProduct?.rate?.formatRate() ?: 0.0} (${currentProduct?.rate_count ?: 0} đánh giá)"
            cvPositionImg.isVisible = (currentProduct?.images?.size ?: 0) > 1
            views.tvPositionImg.text = "1/${currentProduct?.images?.size}"
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
                sizeId = currentSize!!._id,
                toppings = toppingAdapter.getToppingsSelect()
            )
        }
        productViewModel.handle(ProductAction.CreateCart(newCartProduct!!))
        currentSoldQuantity = 1
    }

    @SuppressLint("SuspiciousIndentation")
    private fun sendCart() {
        var total = 0.0

        val toppingCarts = arrayListOf<ToppingCart>()
            toppingAdapter.getToppingsSelect()?.forEach{
                toppingCarts.add(ToppingCart.toToppingCart(it))
                total += it.price * it.quantity
            }

        val productCarts = arrayListOf<ProductCart>().apply {
            add(currentProduct.let {
                ProductCart(
                    _id = "",
                    productId = it!!,
                    purchase_quantity = currentSoldQuantity!!,
                    sizeId = currentSize!!,
                    toppings = toppingCarts
                )
            })
        }
        total += (currentSize!!.size_price * currentSoldQuantity!!)

        val cartResponse: CartResponse? = currentProduct?.let {
            CartResponse(
                _id = "",
                userId = null,
                couponId = null,
                products = productCarts,
                total = total,
                totalCoupon = null,
                createdAt = Date().convertDateToStringFormat(StringUltis.dateIso8601Format),
                updatedAt = Date().convertDateToStringFormat(StringUltis.dateIso8601Format),
            )
        }

        val intent = Intent(requireContext(), PayNowActivity::class.java)
        intent.putExtras(Bundle().apply {
            this.putSerializable("data", cartResponse)
        })
        startActivity(intent)
    }

    private fun enableAnimation(view: LottieAnimationView, raw: Int) {
        view.visibility = View.VISIBLE
        view.setAnimation(raw)
        view.playAnimation()
    }
    private fun cancelAnimation(view: LottieAnimationView) {
        view.visibility = View.GONE
        view.clearAnimation()
        view.cancelAnimation()
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

    override fun getBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentFoodDetailBinding {
        return FragmentFoodDetailBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun invalidate(): Unit = withState(productViewModel) {
        views.swipeLoading.isRefreshing = it.asyncTopProduct is Loading || it.asynGetSizeProduct is Loading
        if (it.curentAddProductToCartResponse is Loading ) { enableAnimation(views.animAddProduct, R.raw.anim_add_to_cart) }

        when (it.curentAddProductToCartResponse) {
            is Success -> {
                it.curentAddProductToCartResponse = Uninitialized
            }
            is Fail ->{
                Toast.makeText(requireContext(), "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show()
                it.curentAddProductToCartResponse = Uninitialized
            }

            else -> {
            }
        }
        when (it.asynGetSizeProduct) {
            is Success -> {
                it.asynGetSizeProduct.invoke()?.let {
                    adapterSize.setData(it)
                    adapterSize.setSelect(currentSize)
                }
            }
            else -> {}
        }
        when (it.asyncGetOneSize) {
            is Success -> {
                it.asyncGetOneSize.invoke()?.let {size ->
//                    sizeId = size._id
//                    views.buttonAddCart.text = "Thêm ${size.size_price.formatCash()}"
//                    views.buttonAddCart.isEnabled = true
                }

                it.asyncGetOneSize = Uninitialized
            }

            else -> {}
        }
        when (it.asyncToppingsProduct) {
            is Success -> {
                it.asyncToppingsProduct.invoke()?.let {toppings ->
                    toppingAdapter.setData(toppings)
                }

                it.asyncGetOneSize = Uninitialized
            }

            else -> {}
        }
        when (it.asyncProduct) {
            is Success -> {
                it.asyncProduct.invoke()?.let { product ->
                    currentProduct = product
                    initUiProduct()
                }
            }

            else -> {}
        }
        when (it.asyncGetFavourite) {
            is Success -> {
                isLiked = true
                views.btnLike.setImageResource(R.drawable.like_full)
                it.asyncGetFavourite = Uninitialized
            }

            is Fail -> {
                isLiked = false
                views.btnLike.setImageResource(R.drawable.like_emty)
                it.asyncGetFavourite = Uninitialized
            }

            else -> {}
        }

        when(it.asyncComments){
            is Success ->{
                commentAdapter.setData(it.asyncComments.invoke())
                views.tvNoComment.isVisible = it.asyncComments.invoke().isNullOrEmpty()
            }
            else ->{

            }
        }
    }

}