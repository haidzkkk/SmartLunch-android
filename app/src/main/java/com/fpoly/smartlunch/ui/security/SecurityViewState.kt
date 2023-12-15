package com.fpoly.smartlunch.ui.security

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.model.VerifyOTPResponse

data class SecurityViewState (
    var asyncLogin: Async<TokenResponse> = Uninitialized,
    var asyncSignUp: Async<VerifyOTPResponse> = Uninitialized,
    var asyncForgotPassword: Async<VerifyOTPResponse> = Uninitialized,
    var asyncUserCurrent: Async<User> = Uninitialized,
    var asyncVerifyOTP: Async<User> = Uninitialized,
    var asyncResetPassword: Async<User> = Uninitialized
): MvRxState