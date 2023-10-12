package com.fpoly.smartlunch.ui.security

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.databinding.ActivityLoginBinding
import javax.inject.Inject

class LoginActivity : PolyBaseActivity<ActivityLoginBinding>(), LoginViewModel.Factory {

    override fun getBinding(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)

    @Inject
    lateinit var loginViewModelFactory: LoginViewModel.Factory

    val navigationController: NavController by lazy { Navigation.findNavController(views.root.findViewById(R.id.fragment_component)) }
    val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyConponent.inject(this)
        super.onCreate(savedInstanceState)


        loginViewModel.observeViewEvents {
            handleLoginViewEvent(it)
        }
    }

    private fun handleLoginViewEvent(it: LoginViewEvent?) {
        when(it){
            is LoginViewEvent.ToFragmentViewEvent -> handleNavigateTo(it.id)
            is LoginViewEvent.ReturnFragmentViewEvent -> handleReturnNavigation()
            else -> {}
        }
    }

    private fun handleNavigateTo(id : Int) {
        navigationController.navigate(id)
    }

    private fun handleReturnNavigation() {
        navigationController.navigateUp()
    }

    override fun create(initialState: LoginViewState): LoginViewModel {
        return  loginViewModelFactory.create(initialState)
    }
}