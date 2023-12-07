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
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.PagingRequestProduct
import com.fpoly.smartlunch.data.model.SortPagingProduct
import com.fpoly.smartlunch.databinding.FragmentSearchBinding
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.home.adapter.ProductPaginationAdapter
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.search.adapter.SearchAdapter
import com.fpoly.smartlunch.ultis.PaginationScrollListenner
import com.fpoly.smartlunch.ultis.hideKeyboard
import com.fpoly.smartlunch.ultis.showKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : PolyBaseFragment<FragmentSearchBinding>(){
    private var myDelayJob: Job? = null

    private lateinit var productAdapter: ProductPaginationAdapter

    private var strSearch = ""
    private var strFilter: String? = null
    private var isSortDesc: Boolean? = null

    private var isSwipeLoading: Boolean = false

    private val productViewModel: ProductViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        listenEvent()
    }

    private fun initUI() {
        productAdapter = ProductPaginationAdapter {
            productViewModel.handle(ProductAction.GetDetailProduct(it))
            productViewModel.handle(ProductAction.GetListSizeProduct(it))
            productViewModel.handle(ProductAction.GetListToppingProduct(it))
            productViewModel.handle(ProductAction.GetListCommentsLimit(it))
            productViewModel.returnDetailProductFragment()
        }
        val linearLayoutManager = LinearLayoutManager(requireContext())
        views.rcv.addOnScrollListener(object :
            PaginationScrollListenner(linearLayoutManager) {
            override fun loadMoreItems() {
                productAdapter.isLoadingOk = true
                productAdapter.curentPage += 1

                val pagingSearch = PagingRequestProduct(10, SortPagingProduct.bought, null, productAdapter.curentPage, strSearch)
                productViewModel.handle(ProductAction.SearchProductByName(pagingSearch))
            }

            override fun isLoading(): Boolean {
                return productAdapter.isLoadingOk
            }

            override fun isLastPage(): Boolean {
                return productAdapter.isLastPage
            }
        })
        views.rcv.adapter = productAdapter
        views.rcv.layoutManager = linearLayoutManager

        strFilter = arguments?.getString("sort")
        if (strFilter != null){
            fetchData()
            setFilterRate()
            context?.hideKeyboard(views.edtTitle)
        }else{
            context?.showKeyboard(views.edtTitle)
        }
    }

    private fun listenEvent() {
        views.swipeLoading.setOnRefreshListener {
            isSwipeLoading = true
            fetchData()
        }
        views.imgBack.setOnClickListener{
            activity?.onBackPressed()
        }
        views.btnSort.setOnClickListener{
            isSortDesc = isSortDesc != true
            views.btnSort.setBackgroundResource(R.drawable.chips)
            fetchData()
        }
        views.btnBestSeller.setOnClickListener{
            strFilter = SortPagingProduct.bought
            fetchData()
            setFilterRate()
        }
        views.btnTopRating.setOnClickListener{
            strFilter = SortPagingProduct.rate
            fetchData()
            setFilterRate()
        }
        views.btnPrice.setOnClickListener{
            strFilter = SortPagingProduct.price
            fetchData()
            setFilterRate()
        }

        views.imgClear.setOnClickListener{
            views.edtTitle.setText("")
        }

        views.edtTitle.doOnTextChanged { text, start, before, count ->
            views.imgClear.isVisible = text.toString().isNotEmpty()
            views.imgLoading.isVisible = text.toString().isNotEmpty()

            if (text.toString().isEmpty()){
                views.tvFinding.text = "Hãy tìm kiếm"
            }else{
                views.tvFinding.text = "Xem kết quả cho: $text"
            }

            if (text.toString().isNotEmpty()){
                myDelayJob?.cancel()
                myDelayJob = CoroutineScope(Dispatchers.Main).launch{
                    delay(500)

                    strSearch = text.toString()
                    resetData()
                    fetchData()
                }
            }
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater,container,false)
    }

    override fun invalidate(): Unit = withState(productViewModel){
        views.swipeLoading.isRefreshing = it.currentProductsSearch is Loading && isSwipeLoading

        when(it.currentProductsSearch){
            is Success ->{
                isSwipeLoading = false
                productAdapter.isLoadingOk = false
                productAdapter.setData(it.currentProductsSearch.invoke()?.docs)
//                views.tvExists.isVisible = it.currentProductsSearch.invoke()?.docs?.isEmpty() ?: true
                it.currentProductsSearch = Uninitialized
            }
            is Fail ->{
//                productAdapter.resetData()
//                views.tvExists.isVisible = true
            }
            is Loading ->{
            }
            else -> {}
        }

        views.imgLoading.isVisible = it.currentProductsSearch is Loading
    }

    private fun setFilterSort(str: String){
        views.btnTopRating.setBackgroundResource(R.drawable.background_border_gray_outline)
        views.btnBestSeller.setBackgroundResource(R.drawable.background_border_gray_outline)
        views.btnPrice.setBackgroundResource(R.drawable.background_border_gray_outline)

        when(str){
            SortPagingProduct.rate ->{
                views.btnTopRating.setBackgroundResource(R.drawable.chips)
            }
            SortPagingProduct.bought ->{
                views.btnBestSeller.setBackgroundResource(R.drawable.chips)
            }
            SortPagingProduct.price ->{
                views.btnPrice.setBackgroundResource(R.drawable.chips)
            }
            else ->{

            }
        }
    }

    private fun setFilterRate(){
        views.btnTopRating.setBackgroundResource(R.drawable.background_border_gray_outline)
        views.btnBestSeller.setBackgroundResource(R.drawable.background_border_gray_outline)
        views.btnPrice.setBackgroundResource(R.drawable.background_border_gray_outline)

        when(strFilter){
            SortPagingProduct.rate ->{
                views.btnTopRating.setBackgroundResource(R.drawable.chips)
            }
            SortPagingProduct.bought ->{
                views.btnBestSeller.setBackgroundResource(R.drawable.chips)
            }
            SortPagingProduct.price ->{
                views.btnPrice.setBackgroundResource(R.drawable.chips)
            }
            else ->{
            }
        }

    }

    fun fetchData(){
        productAdapter.resetData()
        productAdapter.isLoadingOk = true
        val strSort = if (isSortDesc == false) "asc" else "desc"
        val pagingSearch = PagingRequestProduct(10, strFilter, strSort, productAdapter.curentPage, strSearch)
        productViewModel.handle(ProductAction.SearchProductByName(pagingSearch))
    }

    private fun resetData() {
        strFilter = null
        isSortDesc = null

        setFilterRate()
        views.btnSort.setBackgroundResource(R.drawable.background_border_gray_outline)

        productAdapter.resetData()
        productAdapter.isLoadingOk = true
    }
}
