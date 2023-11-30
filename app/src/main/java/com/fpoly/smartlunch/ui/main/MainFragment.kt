package com.fpoly.smartlunch.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.airbnb.mvrx.activityViewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentMainBinding
import com.fpoly.smartlunch.ui.main.home.HomeViewEvent
import com.fpoly.smartlunch.ui.main.home.HomeViewModel
import com.fpoly.smartlunch.ui.main.product.ProductEvent
import com.fpoly.smartlunch.ui.main.product.ProductViewModel
import com.fpoly.smartlunch.ultis.addFragmentToBackStack
import com.fpoly.smartlunch.ultis.changeLanguage
import com.fpoly.smartlunch.ultis.changeMode
import javax.inject.Inject

class MainFragment : PolyBaseFragment<FragmentMainBinding>(){
    private val homeViewModel: HomeViewModel by activityViewModel()
    private val productViewModel: ProductViewModel by activityViewModel()
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        handleViewModel()
        setupBottomNavigation()
    }
    private fun handleViewModel() {
        homeViewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }

        productViewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }
    }
    private fun setupBottomNavigation() {
        homeViewModel.returnHomeFragment()
        views.bottomNav.apply {
            this.setItemSelected(R.id.menu_home)
            this.setOnItemSelectedListener { itemId ->
                when (itemId) {
                    R.id.menu_home -> homeViewModel.returnHomeFragment()
                    R.id.menu_favourite -> homeViewModel.returnFavouriteFragment()
                    R.id.menu_card -> homeViewModel.returnCouponsFragment()
                    R.id.menu_order -> homeViewModel.returnOrderFragment()
                    R.id.menu_profile -> homeViewModel.returnProfileFragment()
                }
            }
            visibilityBottomNav(true)
        }
    }

    private fun handleEvent(event: PolyViewEvent) {
        when (event) {
            is HomeViewEvent -> {
                when (event) {
                    is HomeViewEvent.ReturnFragment<*> -> {
                        addFragmentToBackStack(
                            R.id.frame_main_layout,
                            event.fragmentClass,
                            event.fragmentClass.simpleName.toString()
                        )
                    }

                    is HomeViewEvent.ReturnFragmentWithArgument<*> -> {
                        addFragmentToBackStack(
                            R.id.frame_main_layout,
                            event.fragmentClass,
                            bundle = event.bundle,
                            tag = event.fragmentClass.simpleName.toString()
                        )
                    }

                    else -> {}
                }
            }

            is ProductEvent -> {
                when (event) {
                    is ProductEvent.ReturnFragment<*> -> {
                        addFragmentToBackStack(
                            R.id.frame_main_layout,
                            event.fragmentClass,
                            event.fragmentClass.simpleName.toString()
                        )
                    }

                    else -> {}
                }
            }
        }

    }

    private fun visibilityBottomNav(isVisible: Boolean) {
        views.bottomNav.isVisible = isVisible
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater,container,false)
    }

    override fun invalidate() {
    }

}