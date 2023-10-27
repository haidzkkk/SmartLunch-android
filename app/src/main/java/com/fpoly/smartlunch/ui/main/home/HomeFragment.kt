package com.fpoly.smartlunch.ui.main.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentHomeBinding
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProduct
import com.fpoly.smartlunch.ui.main.home.adapter.AdapterProductVer
import com.fpoly.smartlunch.ui.main.product.ProductAction
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ui.main.profile.UserViewModel
import javax.inject.Inject

class HomeFragment @Inject constructor() : PolyBaseFragment<FragmentHomeBinding>() {
    companion object{
        const val TAG = "HomeFragment"
    }

    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()
    private val userViewModel: UserViewModel by activityViewModel()

    private lateinit var adapter : AdapterProduct
    private lateinit var adapterver : AdapterProductVer

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        bottom_Sheet()
        productViewModel.handle(ProductAction.GetListProduct)
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.returnVisibleBottomNav(true)
    }

    private fun bottom_Sheet(){
        views.floatBottomSheet.setOnClickListener {
            bottomSheet()
        }
    }
    private fun bottomSheet() {
        val bottomSheetFragment = HomeBottomSheet()
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
    private fun initUi() {

        adapter = AdapterProduct{
            productViewModel.handle(ProductAction.oneProduct(it))
            homeViewModel.returnDetailProductFragment()
        }
        views.recyclerViewHoz.adapter = adapter
        adapterver = AdapterProductVer{
            productViewModel.handle(ProductAction.oneProduct(it))
            homeViewModel.returnDetailProductFragment()
        }
        views.recyclerViewVer.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL , false)
        views.recyclerViewVer.adapter = adapterver

    }

    override fun invalidate(): Unit = withState(productViewModel) {
        when (it.products) {
            is Loading -> Log.e("TAG", "HomeFragment view state: Loading")
            is Success -> {
                adapter.setData(it.products.invoke()?.docs)
                adapterver.products = it.products.invoke()?.docs!!
                adapterver.notifyDataSetChanged()
            }
            else -> {

            }
        }
    }
}