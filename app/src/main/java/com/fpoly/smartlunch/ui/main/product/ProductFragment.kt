package com.fpoly.smartlunch.ui.main.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Product
import com.fpoly.smartlunch.data.model.ProductsResponse
import com.fpoly.smartlunch.data.model.Size
import com.fpoly.smartlunch.databinding.FragmentFoodDetailBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProductVer
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterSize
import javax.inject.Inject


class ProductFragment @Inject constructor() : PolyBaseFragment<FragmentFoodDetailBinding>() {

    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()
    private  var currentSoldQuantity: Int? = null
    private lateinit var adapterSize: AdapterSize


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
    }


    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    private fun addCart(){

    }
    private fun quanlity_product(initialQuantity: Int) {
        var currentQuantity = initialQuantity // Khởi tạo số lượng hiện tại với giá trị ban đầu

        views.someIdQuality.text = currentQuantity.toString()


        views.linearMinu2.setOnClickListener {

            currentQuantity++
            views.someIdQuality.text = currentQuantity.toString()
        }


        views.linearMinu1.setOnClickListener {

            if (currentQuantity > 1) {
                currentQuantity--
                views.someIdQuality.text = currentQuantity.toString()
            }
        }
    }



    private fun initUiSize() {
        adapterSize = AdapterSize{idSize ->

        }
        views.rcvSize.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        views.rcvSize.adapter = adapterSize
    }

    private fun initUi(product: Product) {

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
    }
}