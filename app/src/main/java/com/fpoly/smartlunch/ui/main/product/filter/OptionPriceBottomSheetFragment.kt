package com.fpoly.smartlunch.ui.main.product.filter

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
import com.fpoly.smartlunch.databinding.FragmentHomeBottomSheetCategoryBinding
import com.fpoly.smartlunch.databinding.FragmentOptionPriceBottomSheetBinding
import com.fpoly.smartlunch.ui.main.home.HomeBottomSheetCategory
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterCategory
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel

class OptionPriceBottomSheetFragment : PolyBaseBottomSheet<FragmentOptionPriceBottomSheetBinding>(){
    private val productViewModel: ProductViewModel by activityViewModel()
    override val isBorderRadiusTop: Boolean
        get() = true
    override val isDraggable: Boolean
        get() = true
    override val isExpanded: Boolean
        get() = false

    companion object{
        const val TAG = "OptionPriceBottomSheetFragment"
        fun newInstance() = OptionPriceBottomSheetFragment()
    }
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOptionPriceBottomSheetBinding {
        return FragmentOptionPriceBottomSheetBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        listenEvent()
    }

    private fun listenEvent() {
        views.lowToUp.setOnClickListener {

        }
        views.upToLow.setOnClickListener {

        }
    }

    private fun initUi(){
        views.appBar.tvTitleToolbar.text="Lựa chọn giá"
    }
    override fun invalidate() : Unit = withState(productViewModel){

    }


}