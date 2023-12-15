package com.fpoly.smartlunch.ui.security

import android.Manifest
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.ActivitySplashScreenBinding
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.main.MainActivity
import com.fpoly.smartlunch.ui.notification.receiver.MyReceiver
import com.fpoly.smartlunch.ultis.MyConfigNotifi
import com.fpoly.smartlunch.ultis.changeLanguage
import com.fpoly.smartlunch.ultis.changeMode
import com.fpoly.smartlunch.ultis.handleLogOut
import com.fpoly.smartlunch.ultis.startActivityWithData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject

class SplashScreenActivity : PolyBaseActivity<ActivitySplashScreenBinding>(),SecurityViewModel.Factory{

    private val viewModel: SecurityViewModel by viewModel()
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        configData()
        viewModel.subscribe(this) {
            handleStateChange(it)
        }
    }

    private fun startActivityWithFCMReciveDataNotifi() {
        val type = intent.extras?.getString("type")
        val idUrl = intent.extras?.getString("idUrl")

        val uriData = intent.data
        if (uriData != null){
            Toast.makeText(this, "link: $uriData", Toast.LENGTH_SHORT).show()
        }

        when(type){
            MyConfigNotifi.TYPE_CHAT ->{
                startMainActivityToBackStack()
                var intent = Intent(this, ChatActivity::class.java)
                startActivityWithData(intent, type, idUrl)
            }
            MyConfigNotifi.TYPE_CALL_ANSWER ->{
                startMainActivityToBackStack()
                var intent = Intent(this, ChatActivity::class.java)
                startActivityWithData(intent, type, idUrl)
            }
            MyConfigNotifi.TYPE_ORDER ->{
                getPendingIntent(this, 1)?.send()
            }
            else ->{
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        finish()
    }
    private fun getPendingIntent(context: Context, action: Int): PendingIntent? {
        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra("notification_action_broadcast", action)
        return PendingIntent.getBroadcast(context.applicationContext, action, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun startMainActivityToBackStack(){
        var intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
    }

    private fun logOutGG() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                Log.w("Logout", "Sign out success")
            }
            .addOnFailureListener { e ->
                Log.w("Logout", "Sign out failed", e)
            }
    }

    private fun handleStateChange(it: SecurityViewState) {
        when (it.asyncUserCurrent) {
            is Success -> {
                startActivityWithFCMReciveDataNotifi()
            }

            is Fail -> {
                handleLogOut()
                startActivity(Intent(this, LoginActivity::class.java))
                logOutGG()
                finish()
            }

            else -> {}
        }
    }

    private fun configData() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
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