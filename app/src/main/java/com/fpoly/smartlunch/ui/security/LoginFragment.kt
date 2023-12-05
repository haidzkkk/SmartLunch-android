package com.fpoly.smartlunch.ui.security

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.UserGGLogin
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentLoginBinding
import com.fpoly.smartlunch.ui.main.MainActivity
import com.fpoly.smartlunch.ultis.MyConfigNotifi.RC_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import javax.inject.Inject


class LoginFragment : PolyBaseFragment<FragmentLoginBinding>(), TextWatcher {
    private val viewModel: SecurityViewModel by activityViewModel()
    private var isFieldsFilled = false
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        listenEvent()
        super.onViewCreated(view, savedInstanceState)
    }


    private fun signInWithGG() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            Log.d("Login", "signInResult: "+account.id+"\n"+account.givenName+"\n"+account.familyName+"\n"+account.email)
            viewModel.handle(SecurityViewAction.LoginGGAction(UserGGLogin(account.id,account.givenName,account.familyName,account.email, account.id)))
        } catch (e: ApiException) {
            Log.w("Login", "signInResult:failed code=" + e.statusCode)
        }
    }


    private fun listenEvent() {
        views.btnLogin.isEnabled = false

        views.edtPassword.setOnEditorActionListener{v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                views.btnLogin.performClick()
            }
            false
        }

        views.btnLogin.setOnClickListener {
            loginSubmit()
        }
        views.btnLoginGg.setOnClickListener {
            signInWithGG()
        }
        views.btnLoginFb.setOnClickListener {
            showButtonFB()
        }
        views.tvSignUp.setOnClickListener {
            viewModel.handleReturnSignUpEvent()
        }
        views.tvForgotPassword.setOnClickListener {
            viewModel.handleReturnForgotPass()
        }
        views.edtPassword.addTextChangedListener(this)
        views.edtEmail.addTextChangedListener(this)
    }

    private fun showButtonFB() {
        val bottomSheet = FacebookBottomSheetDialog()
        bottomSheet.show(requireActivity().supportFragmentManager, "FacebookBottomSheet")
    }

    private fun loginSubmit() {
        val username = views.edtEmail.text.toString().trim()
        val password = views.edtPassword.text.toString().trim()

        if (validateLoginInput(username, password)) {
            viewModel.handle(SecurityViewAction.LoginAction(username, password))
        }
    }

    private fun validateLoginInput() {
        val username = views.edtEmail.text.toString().trim()
        val password = views.edtPassword.text.toString().trim()
        isFieldsFilled = username.isNotEmpty() && password.isNotEmpty()
        views.btnLogin.isEnabled = isFieldsFilled
    }

    private fun validateLoginInput(username: String, password: String): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            views.tilEmail.error = getString(R.string.username_not_empty)
            isValid = false
        } else {
            views.tilEmail.error = null
        }

        if (password.isEmpty()) {
            views.tilPassword.error = getString(R.string.password_not_empty)
            isValid = false
        } else {
            views.tilPassword.error = null
        }

        return isValid
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncLogin) {
            is Success -> {
                it.asyncLogin.invoke()?.let { token ->
                    token.accessToken?.let { accessToken ->
                        sessionManager.saveAuthTokenAccess(
                            accessToken
                        )
                    }
                    token.refreshToken?.let { refreshToken ->
                        sessionManager.saveAuthTokenRefresh(
                            refreshToken
                        )
                    }
                }
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }

            is Fail -> {
                views.tilPassword.error = getString(R.string.login_fail)
            }

            else -> {}
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding =
        FragmentLoginBinding.inflate(layoutInflater)

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        validateLoginInput()
    }

}



