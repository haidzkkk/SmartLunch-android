package com.fpoly.smartlunch.ui.main.love

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentHistoryRatingProductBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.love.Adapter.ProductFavouriteAdapter
import com.fpoly.smartlunch.ui.main.order.ProductOrderAdapter
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel

class HistoryRatingProductFragment : PolyBaseFragment<FragmentHistoryRatingProductBinding>(){
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private lateinit var adapter: ProductOrderAdapter
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoryRatingProductBinding {
        return FragmentHistoryRatingProductBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        listenEvent()
    }

    private fun initUI() {
        adapter = ProductOrderAdapter {
            productViewModel.handle(ProductAction.GetDetailProduct(it.productId))
            productViewModel.handle(ProductAction.GetListSizeProduct(it.productId))
            productViewModel.handle(ProductAction.GetListToppingProduct(it.productId))
            productViewModel.handle(ProductAction.GetListComments(it.productId, limit = 2))
            homeViewModel.returnDetailProductFragment()
        }
        views.rcv.layoutManager = LinearLayoutManager(requireContext())
        views.rcv.adapter = adapter
        productViewModel.handle(ProductAction.GetAllHistoryProduct)
    }

    private fun listenEvent(){

    }

    override fun invalidate(): Unit = withState(productViewModel){
        when(it.asyncHistories){
            is Success ->{
                adapter.setData(it.asyncHistories.invoke())
            }

            else -> {}
        }
    }

}