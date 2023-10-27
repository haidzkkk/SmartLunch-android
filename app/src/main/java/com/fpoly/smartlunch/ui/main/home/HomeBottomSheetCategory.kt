package com.fpoly.smartlunch.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.databinding.BottomsheetFragmentHomeBinding
import com.fpoly.smartlunch.databinding.FragmentHomeBottomSheetCategoryBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCart
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCategory
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel


class HomeBottomSheetCategory : PolyBaseBottomSheet<FragmentHomeBottomSheetCategoryBinding>() {

    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private lateinit var adapterCategory : AdapterCategory
    // mặc định là như này
    override val isBorderRadiusTop: Boolean
        get() = true
    override val isDraggable: Boolean
        get() = true
    override val isExpanded: Boolean
        get() = false

    companion object{
        const val TAG = "HomeBottomSheetCategory"
        fun newInstance() = HomeBottomSheetCategory()
    }
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBottomSheetCategoryBinding {
      return FragmentHomeBottomSheetCategoryBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi(){
        adapterCategory = AdapterCategory{
            productViewModel.handle(ProductAction.getAllProductByIdCategory(it))
            homeViewModel.returnProductListFragment()
        }
        views.rcvCategory.adapter = adapterCategory

    }
    override fun invalidate() : Unit = withState(productViewModel){
       when(it.category){
           is Success ->{
               adapterCategory.categories = it.category.invoke()?.docs!!
               adapterCategory.notifyDataSetChanged()
           }

           else -> {}
       }
    }


}