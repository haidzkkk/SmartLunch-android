package com.fpoly.smartlunch.ui.main.product

import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState

import com.fpoly.smartlunch.core.PolyBaseFragment

import com.fpoly.smartlunch.databinding.FragmentProductListBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCategory
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterListProduct
import javax.inject.Inject




class ProductListFragment @Inject constructor() : PolyBaseFragment<FragmentProductListBinding>() {

    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()

    private lateinit var adapterList : AdapterListProduct
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProductListBinding {
      return FragmentProductListBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        initUiProduct()
        listenEvent()
    }

   private fun listenEvent(){
       views.nameList.tvTitleToolbar.text = "Danh sách"
   }

    private fun initUiProduct() {
           adapterList = AdapterListProduct{
               productViewModel.handle(ProductAction.oneProduct(it))
               homeViewModel.returnDetailProductFragment()
        }
        views.rcvProduct.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        views.rcvProduct.adapter = adapterList
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    override fun invalidate() : Unit = withState(productViewModel){

        when(it.getAllProductByIdCategory){
            is Success ->{
                adapterList.setData(it.getAllProductByIdCategory.invoke())
            }
            else -> {}
        }
    }

}