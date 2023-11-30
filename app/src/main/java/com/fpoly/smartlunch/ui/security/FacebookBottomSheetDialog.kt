package com.fpoly.smartlunch.ui.security

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.data.model.UserGGLogin
import com.fpoly.smartlunch.databinding.FragmentFacebookBottomSheetDialogBinding
import com.fpoly.smartlunch.ultis.showSnackbar
import org.json.JSONObject

class FacebookBottomSheetDialog : PolyBaseBottomSheet<FragmentFacebookBottomSheetDialogBinding>() {
    private lateinit var callbackManager: CallbackManager
    private val securityViewModel: SecurityViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookLoginSuccess(loginResult.accessToken)
                }

                override fun onCancel() {
                    handleFacebookLoginCancel()
                }

                override fun onError(exception: FacebookException) {
                    handleFacebookLoginError(exception)
                }
            })

        views.loginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(requireActivity(), listOf("public_profile"))
            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookLoginSuccess(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject: JSONObject?, graphResponse: GraphResponse? ->
            if (jsonObject != null) {
                val firstName = jsonObject.optString("first_name")
                val lastName = jsonObject.optString("last_name")
                val email = jsonObject.optString("email")
                val fullName = "$firstName $lastName"
                val userInfo = "Welcome, $fullName!"
                securityViewModel.handle(SecurityViewAction.LoginFBAction(UserGGLogin(accessToken.userId,firstName,lastName,email)))
                showSnackbar(requireView(), userInfo, true, "Ok", {})
                Log.d("FacebookBottomSheetDialog", "handleFacebookLogin - Welcome, $fullName!")
            } else {
                showSnackbar(requireView(), "Failed to get user information", true, "Ok", {})
                Log.d("FacebookBottomSheetDialog", "handleFacebookLogin - Failed to get user information")
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun handleFacebookLoginCancel() {
        dismiss()
    }

    private fun handleFacebookLoginError(exception: FacebookException) {
        showSnackbar(requireView(), "Đăng nhập thất bại", true, "Ok", {})
        Log.d("FacebookBottomSheetDialog", "handleFacebookLoginError: $exception")
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFacebookBottomSheetDialogBinding {
        return FragmentFacebookBottomSheetDialogBinding.inflate(inflater, container, false)
    }

    override fun invalidate() {
    }
}
