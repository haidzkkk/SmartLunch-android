package com.fpoly.smartlunch.ui.security

import VerifyOTPFragment
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.ActivityLoginBinding
import com.fpoly.smartlunch.ui.security.onboarding.ViewPagerFragment
import com.fpoly.smartlunch.ultis.addFragmentToBackStack
import com.fpoly.smartlunch.ultis.changeLanguage
import com.fpoly.smartlunch.ultis.changeMode
import com.fpoly.smartlunch.ultis.popBackStackAndShowPrevious
import javax.inject.Inject

class LoginActivity : PolyBaseActivity<ActivityLoginBinding>(), SecurityViewModel.Factory {


    val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        setupUi()
    }

    private fun setupUi() {
        supportFragmentManager.commit {
            if (sessionManager.getOnBoardingFinished()) {
                add<LoginFragment>(R.id.frame_layout).addToBackStack(LoginFragment::class.java.simpleName)
            } else {
                add<ViewPagerFragment>(R.id.frame_layout).addToBackStack(ViewPagerFragment::class.java.simpleName)
            }
        }
        viewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }
        print(viewModel.getString())
        changeMode(sessionManager.fetchDarkMode())
        changeLanguage(sessionManager.fetchLanguage())
    }

    private fun handleEvent(event: SecurityViewEvent) {
        when (event) {
            is SecurityViewEvent.ReturnSignUpEvent -> {
                addFragmentToBackStack(R.id.frame_layout, SignUpFragment::class.java)
            }

            is SecurityViewEvent.ReturnResetPassEvent -> {
                addFragmentToBackStack(R.id.frame_layout, ResetPasswordFragment::class.java)
            }

            is SecurityViewEvent.ReturnForgotPassEvent -> {
                addFragmentToBackStack(R.id.frame_layout, ForgotPasswordFragment::class.java)
            }

            is SecurityViewEvent.ReturnLoginEvent -> {
                addFragmentToBackStack(R.id.frame_layout, LoginFragment::class.java)
            }

            is SecurityViewEvent.ReturnVerifyOTPEvent -> {
                addFragmentToBackStack(R.id.frame_layout, VerifyOTPFragment::class.java)
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        popBackStackAndShowPrevious()
    }

    override fun getBinding(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityViewModelFactory.create(initialState)
    }

}