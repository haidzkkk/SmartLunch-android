package com.fpoly.smartlunch.ui.security

import android.content.Intent
import android.os.Bundle
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.ActivitySplashScreenBinding
import com.fpoly.smartlunch.ui.main.MainActivity
import com.fpoly.smartlunch.ultis.changeLanguage
import com.fpoly.smartlunch.ultis.changeMode
import com.fpoly.smartlunch.ultis.handleLogOut
import javax.inject.Inject

class SplashScreenActivity : PolyBaseActivity<ActivitySplashScreenBinding>(),SecurityViewModel.Factory{

    private val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        viewModel.subscribe(this) {
            handleStateChange(it)
        }
        configData()
    }

    private fun handleStateChange(it: SecurityViewState) {
        when (it.asyncUserCurrent) {
            is Success -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            is Fail -> {
                handleLogOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            else -> {}
        }
    }

    private fun configData() {
        sessionManager.fetchDarkMode().let { changeMode(it) }
        sessionManager.fetchLanguage().let { changeLanguage( it ) }
    }

    override fun getBinding(): ActivitySplashScreenBinding {
        return ActivitySplashScreenBinding.inflate(layoutInflater)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityViewModelFactory.create(initialState)
    }
}