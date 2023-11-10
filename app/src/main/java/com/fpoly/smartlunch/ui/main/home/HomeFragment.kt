package com.fpoly.smartlunch.ui.main.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentHomeBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProductVer
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductEvent
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import javax.inject.Inject

class HomeFragment @Inject constructor() : PolyBaseFragment<FragmentHomeBinding>() {
    companion object {
        const val TAG = "HomeFragment"
    }

    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private lateinit var adapter: AdapterProduct
    private lateinit var adapterver: AdapterProductVer

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        listenEvent()
    }

    private fun initUi() {
        updateCart()
        //setup rcyH
        adapter = AdapterProduct {
            onItemProductClickListener(it)
        }
        views.recyclerViewHoz.adapter = adapter

        //setup rcyV
        adapterver = AdapterProductVer {
            onItemProductClickListener(it)
        }
        views.recyclerViewVer.adapter = adapterver
    }

    private fun updateCart() {
        productViewModel.handle(ProductAction.GetOneCartById)
    }

    private fun listenEvent() {
        productViewModel.observeViewEvents {
            handleViewEvent(it)
        }

        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetListProduct)
            productViewModel.handle(ProductAction.GetListTopProduct)
        }

        views.btnDefault.setOnClickListener {
            openCategoryBottomSheet()
        }
        views.floatBottomSheet.setOnClickListener {
            openCartBottomSheet()
        }
    }

    private fun handleViewEvent(event: ProductEvent) {
    when(event){
        is ProductEvent.UpdateCart -> updateCart()
    }
    }

    private fun openCartBottomSheet() {
        val bottomSheetFragment = HomeBottomSheet()
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    private fun openCategoryBottomSheet() {
        val bottomSheetFragment = HomeBottomSheetCategory()
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    private fun onItemProductClickListener(productId:String) {
        productViewModel.handle(ProductAction.GetDetailProduct(productId))
        homeViewModel.returnDetailProductFragment()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(true)
    }

    override fun invalidate(): Unit = withState(productViewModel) {
        views.swipeLoading.isRefreshing = it.isSwipeLoading

        when (it.products) {
            is Success -> {
                views.floatBottomSheet.isVisible = true
                adapterver.setData(it.products.invoke()?.docs)
            }
            else -> {
            }
        }
        when (it.asyncTopProduct) {
            is Success -> {
                adapter.setData(it.asyncTopProduct.invoke())
            }
            else -> {
            }
        }
        when (it.getOneCartById) {
            is Success -> {
                if (it.getOneCartById.invoke()?.products?.size!! > 0) {
                    views.layoutCart.visibility = View.VISIBLE
                }else{
                    views.layoutCart.visibility = View.GONE
                }
            }
            is Fail -> {
                views.layoutCart.visibility = View.GONE
            }
            else -> {
                views.layoutCart.visibility = View.GONE
            }
        }
    }

}