package com.fpoly.smartlunch.ui.main.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentCurrentOrdersBinding
import com.fpoly.smartlunch.ui.main.product.ProductViewModel

class CurrentOrdersFragment : PolyBaseFragment<FragmentCurrentOrdersBinding>(){
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCurrentOrdersBinding {
        return FragmentCurrentOrdersBinding.inflate(inflater,container,false)
    }

    private val productViewModel: ProductViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
    }



    override fun invalidate():Unit= withState(productViewModel){
        when(it.asyncOrderings){
            is Success -> {
                val adapter = OrderAdapter()
                views.rcyOrder.adapter = adapter
                adapter.setData(it.asyncOrderings.invoke())
            }

            else -> {}
        }
    }

}