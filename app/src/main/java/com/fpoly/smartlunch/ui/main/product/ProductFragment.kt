package com.fpoly.smartlunch.ui.main.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.CartRequest
import com.fpoly.smartlunch.data.model.Notify
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.databinding.FragmentFoodDetailBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterSize
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import com.fpoly.smartlunch.ultis.showUtilDialog
import javax.inject.Inject


class ProductFragment @Inject constructor() : PolyBaseFragment<FragmentFoodDetailBinding>() {

    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()
    private val userViewModel : UserViewModel by activityViewModel()
    private  var currentSoldQuantity: Int? = null
    private lateinit var adapterSize: AdapterSize
    private var currentProduct: Product? = null
    private  var sizeCurrent : String? = null

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFoodDetailBinding {
        return FragmentFoodDetailBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUiSize()
        productViewModel.handle(ProductAction.GetListSize)
        views.buttonAddCart.setOnClickListener {
            addCart()
        }
    }


    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    private fun addCart() {
        val product = currentProduct
        val sizeId =
            withState(productViewModel){
                 it.oneSize.invoke()?._id

            }
            val newCart = product?.let {
                CartRequest(
                    productId = it._id,
                    image = "aaaa",
                    product_name = product.product_name,
                    product_price = product.product_price,
                    purchase_quantity = currentSoldQuantity!!,
                    sizeId = sizeId!!
                )
            }

            withState(userViewModel){
                val userId = it.asyncCurrentUser.invoke()?._id
                if (userId != null){
                    productViewModel.handle(ProductAction.CreateCart(userId, newCart!!))
                }

            }

    }
    private fun quanlity_product(initialQuantity: Int) {
         currentSoldQuantity = initialQuantity // Khởi tạo số lượng hiện tại với giá trị ban đầu

        views.someIdQuality.text = currentSoldQuantity.toString()


        views.linearMinu2.setOnClickListener {

            currentSoldQuantity = currentSoldQuantity!! + 1
            views.someIdQuality.text = currentSoldQuantity.toString()
        }


        views.linearMinu1.setOnClickListener {
            if (currentSoldQuantity!! > 1) {
                currentSoldQuantity = currentSoldQuantity!! - 1
                views.someIdQuality.text = currentSoldQuantity.toString()
            }
        }
    }



    private fun initUiSize() {
        adapterSize = AdapterSize{ idSize ->
            productViewModel.handle(ProductAction.oneSize(idSize))

        }
        views.rcvSize.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        views.rcvSize.adapter = adapterSize
    }


    private fun initUi(product: Product) {
        currentProduct = product
        views.apply {
            NameDetailFood.text = product.product_name
            priceDetailFood.text = product.product_price.toString()+"đ"
            descFood.text = product.description
            someIdQuality.text = product.sold_quantity.toString()
        }
        quanlity_product(product.sold_quantity)
    }

    override fun invalidate() : Unit = withState(productViewModel) {
        when(it.size) {
            is Loading -> {
                Log.e("TAG", "HomeFragment view state: Loading")
            }

             is Success -> {
                it.product.invoke()?.let { product ->
                    initUi(product)
                }
                 adapterSize.Listsize = it.size.invoke()!!
                 adapterSize.notifyDataSetChanged()
            }
            else -> {}
        }
        when(it.asyncCreateCart){
            is Success ->{
                activity?.showUtilDialog(
                    Notify("Thêm vào giỏ hàng thành công","${it.asyncCreateCart.invoke()?.products!![0]?.product_name}"
                        ,"Kiểm tra tại giỏ hàng", R.raw.animation_successfully)
                )
                activity?.supportFragmentManager?.popBackStack()
                productViewModel.handleRemoveAsyncCreateCart()
            }
            else -> {}
        }
    }
}