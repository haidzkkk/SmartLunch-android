package com.fpoly.smartlunch.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentHomeBinding
import javax.inject.Inject

class HomeFragment @Inject constructor() : PolyBaseFragment<FragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by activityViewModel()

    private val testViewModel : TestViewModel by lazy{ ViewModelProvider(requireActivity()).get(
        TestViewModel::class.java) }
    private val testViewModelMvRx: TestViewModelMvRx by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("TAG", "fragment: ${homeViewModel.hashCode()}")
        Log.e("TAG", "testViewModel: ${testViewModel.hashCode()}")
        Log.e("TAG", "testViewModelMvRx: ${testViewModelMvRx.hashCode()}")

        homeViewModel.testEvent()

        homeViewModel.observeViewEvents {
            Log.e("TAG", "HomeFragment viewEvent: $it" )
        }

        homeViewModel.handle(HomeViewAction.GetUserViewAction)
    }

    override fun invalidate(): Unit = withState(homeViewModel){
        when(it.users){
            is Loading -> Log.e("TAG", "HomeFragment view state: Loading" )
            is Success -> Log.e("TAG", "HomeFragment view state: Success: ${it.users.invoke() }" )
            else -> {
                Log.e("TAG", "HomeFragment view state: else" )
            }
        }
    }
}
