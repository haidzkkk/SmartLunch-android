package com.fpoly.smartlunch.ui.main.home


import android.annotation.SuppressLint



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
import javax.inject.Inject

class HomeFragment @Inject constructor() : PolyBaseFragment<FragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by activityViewModel()
    private lateinit var adapter : AdapterProduct
    private lateinit var adapterver : AdapterProductVer

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initUi()
        bottom_Sheet()
        homeViewModel.observeViewEvents {
            when(it){
                is HomeViewEvent.testViewEvent -> Log.e("TAG", "HomeFragment viewEvent: $it" )
                else -> {}
            }
        }
        homeViewModel.handle(HomeViewAction.GetListProduct)
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

        adapter = AdapterProduct(requireContext())
        views.recyclerViewHoz.layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL , false)
        views.recyclerViewHoz.adapter = adapter

        adapterver = AdapterProductVer(requireContext())
        views.recyclerViewVer.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL , false)
        views.recyclerViewVer.adapter = adapterver

      

    }

    override fun invalidate(): Unit = withState(homeViewModel) {
        when (it.products) {
            is Loading -> Log.e("TAG", "HomeFragment view state: Loading")
            is Success -> {
                adapter.products = it.products.invoke()?.docs!!

                adapterver.products = it.products.invoke()?.docs!!
                adapter.notifyDataSetChanged()
                adapterver.notifyDataSetChanged()
//                Log.d("bbb","mmmm"+adapter.products)
            }

            else -> {

            }
        }
    }
}
