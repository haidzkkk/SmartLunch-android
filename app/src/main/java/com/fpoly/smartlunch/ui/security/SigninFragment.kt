package com.fpoly.smartlunch.ui.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentSigninBinding

class SigninFragment : PolyBaseFragment<FragmentSigninBinding>(){
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(layoutInflater)
    }

    val loginViewModel: LoginViewModel by activityViewModel()

    override fun invalidate() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.tvBack.setOnClickListener{
            loginViewModel.handleBackToFragment()
        }
    }

}
