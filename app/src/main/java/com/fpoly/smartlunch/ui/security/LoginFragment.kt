package com.fpoly.smartlunch.ui.security

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentLoginBinding
import com.fpoly.smartlunch.ui.main.MainActivity
import com.fpoly.smartlunch.ultis.checkNull
import javax.inject.Inject

class LoginFragment : PolyBaseFragment<FragmentLoginBinding>(){
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding
    =   FragmentLoginBinding.inflate(layoutInflater)

    @Inject
    public lateinit var sessionManager: SessionManager
    val loginViewModel: LoginViewModel by activityViewModel()

    override fun invalidate(): Unit = withState(loginViewModel){
        when(it.asyncLogin){
            is Success -> {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                sessionManager.saveAuthTokenAccess(it.asyncLogin.invoke()!!.accessToken ?: "")
                requireActivity().finishAffinity()
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            }
            is Fail -> {
                Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }

        views.viewLoading.apply {
            this.isClickable = it.asyncLogin is Loading
            this.isFocusable = it.asyncLogin is Loading
            this.isVisible = it.asyncLogin is Loading
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyConponent.inject(this)
        super.onViewCreated(view, savedInstanceState)

        listenClickUI()
    }


    private fun listenClickUI() {
        views.btnLogin.setOnClickListener{
            if (views.edtUsername.checkNull(resources) or
                views.edtPassword.checkNull(resources))
            else handleLogin()
        }

        views.tvSignup.setOnClickListener{
            loginViewModel.handleFragmentSignin()
        }
    }

    private fun handleLogin() {
        loginViewModel.handle(LoginViewAction.LoginAction(views.edtUsername.text.toString(), views.edtPassword.text.toString()))
    }



}