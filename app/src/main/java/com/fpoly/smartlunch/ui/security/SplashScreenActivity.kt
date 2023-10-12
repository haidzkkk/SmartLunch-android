package com.fpoly.smartlunch.ui.security

import android.content.Intent
import android.os.Bundle
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.ActivitySplashScreenBinding
import com.fpoly.smartlunch.ultis.changeLangue
import com.fpoly.smartlunch.ultis.changeMode
import javax.inject.Inject

class SplashScreenActivity : PolyBaseActivity<ActivitySplashScreenBinding>(){
    override fun getBinding(): ActivitySplashScreenBinding {
        return ActivitySplashScreenBinding.inflate(layoutInflater)
    }

    @Inject
    public lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyConponent.inject(this)
        super.onCreate(savedInstanceState)

        configData()

        Thread{
            Thread.sleep(2000)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }.start()
    }

    private fun configData() {
        sessionManager.fetchDarkMode().let {   changeMode(it ?: false) }
        sessionManager.fetchLanguage().let { changeLangue(resources, it ?: "en") }
    }
}