package com.fpoly.smartlunch.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.databinding.BottomsheetFragmentHomeBinding

class HomeBottomSheet : PolyBaseBottomSheet<BottomsheetFragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by activityViewModel()

    // mặc định là như này
    override val isBorderRadiusTop: Boolean
        get() = true
    override val isDraggable: Boolean
        get() = true
    override val isExpanded: Boolean
        get() = false

    companion object{
        const val TAG = "HomeBottomSheet"
        fun newInstance() = HomeBottomSheet()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomsheetFragmentHomeBinding = BottomsheetFragmentHomeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.handle(HomeViewAction.TestViewAction)

        views.btnDismiss.setOnClickListener{
            this.dismiss()
        }
    }

    override fun invalidate(): Unit = withState(homeViewModel){
        when(it.test){
            is Success -> views.tvTest.text = it.test.invoke()
            else -> {
                Log.e("TAG", "HomeFragment view state: else" )
            }
        }
    }
}