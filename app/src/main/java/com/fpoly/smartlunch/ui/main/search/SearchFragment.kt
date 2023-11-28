package com.fpoly.smartlunch.ui.main.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentSearchBinding
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.search.adapter.SearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : PolyBaseFragment<FragmentSearchBinding>(){
    private var myDelayJob: Job? = null

    lateinit var adapter: SearchAdapter
    private val productViewModel: ProductViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        listenEvent()
    }

    private fun initUI() {
        views.edtTitle.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(views.edtTitle, InputMethodManager.SHOW_IMPLICIT)

        adapter = SearchAdapter{
            homeViewModel.returnDetailProductFragment()
            productViewModel.handle(ProductAction.GetDetailProduct(it._id))
        }
        views.rcv.adapter = adapter
        views.rcv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun listenEvent() {
        views.imgBack.setOnClickListener{
            activity?.onBackPressed()
        }

        views.imgClear.setOnClickListener{
            views.edtTitle.setText("")
        }

        views.edtTitle.doOnTextChanged { text, start, before, count ->
            views.imgClear.isVisible = text.toString().length != 0

            if (text.toString().length == 0){
                views.tvFinding.text = "Hãy tìm kiếm"
            }else{
                views.tvFinding.text = "Xem kết quả cho: $text"
            }

            views.imgLoading.isVisible = text.toString().length != 0

            if (text.toString().isNotEmpty()){
                myDelayJob?.cancel()
                myDelayJob = CoroutineScope(Dispatchers.Main).launch{
                    delay(500)
                    productViewModel.handle(ProductAction.SearchProductByName(text.toString()))
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        homeViewModel.returnVisibleBottomNav(true)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater,container,false)
    }

    override fun invalidate(): Unit = withState(productViewModel){
        when(it.currentProductsSearch){
            is Success ->{
                adapter.setData(it.currentProductsSearch.invoke())
                views.tvExists.isVisible = it.currentProductsSearch.invoke().isEmpty()
            }
            is Fail ->{
                adapter.clearData()
                views.tvExists.isVisible = true
            }
            is Loading ->{
                adapter.clearData()
            }
            else -> {}
        }

        views.imgLoading.isVisible = it.currentProductsSearch is Loading
    }
}
