package com.fpoly.smartlunch.ui.main.love

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentFavouriteProductsfragmentBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProductVer
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel

class FavouriteProductsFragment : PolyBaseFragment<FragmentFavouriteProductsfragmentBinding>(){
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private var adapter: AdapterProduct? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        listenEvent()

    }

    private fun initUi() {
        adapter = AdapterProduct {
            onItemProductClickListener(it)
        }
        views.recyclerViewHoz.adapter = adapter
    }

    private fun listenEvent() {
        views.swipeLoading.setOnRefreshListener {
            productViewModel.handle(ProductAction.GetAllFavouriteProduct)
        }
    }
    private fun onItemProductClickListener(productId:String) {
        productViewModel.handle(ProductAction.GetDetailProduct(productId))
        homeViewModel.returnDetailProductFragment()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouriteProductsfragmentBinding {
        return FragmentFavouriteProductsfragmentBinding.inflate(inflater,container,false)
    }

    override fun invalidate():Unit = withState(productViewModel) {
        views.swipeLoading.isRefreshing = it.asyncFavourites is Loading

        when(it.asyncFavourites){
            is Success -> {
                adapter?.setData(it.asyncFavourites.invoke())
            }
            else -> {
            }
        }
    }

}