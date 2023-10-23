package com.fpoly.smartlunch.ui.security

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.Data
import com.fpoly.smartlunch.data.model.ResetPasswordRequest
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.model.UserRequest
import com.fpoly.smartlunch.data.model.VerifyOTPRequest

sealed class SecurityViewAction : PolyViewAction {
    data class LoginAction(val userName: String, var password: String) : SecurityViewAction()
    data class SaveTokenAction(val token: TokenResponse) : SecurityViewAction()
    data class SignupAction(val user: UserRequest) : SecurityViewAction()
    data class VerifyOTPAction(val verifyOTPRequest: VerifyOTPRequest) : SecurityViewAction()
    data class VerifyOTPResetPassAction(val verifyOTPRequest: VerifyOTPRequest) : SecurityViewAction()
    data class ResendOTPCode(val resendOTPCode: Data): SecurityViewAction()
    data class ResendResetPassOTPCode(val resendOTPCode: Data): SecurityViewAction()
    data class ForgotPassword(val email: String): SecurityViewAction()
    data class ResetPassword(val resetPasswordRequest: ResetPasswordRequest):SecurityViewAction()

}