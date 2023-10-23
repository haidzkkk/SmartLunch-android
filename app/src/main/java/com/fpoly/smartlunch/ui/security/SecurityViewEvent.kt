package com.fpoly.smartlunch.ui.security

import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.data.model.User

sealed class SecurityViewEvent:PolyViewEvent {
    object ReturnLoginEvent:SecurityViewEvent()
    object ReturnVerifyOTPEvent:SecurityViewEvent()
    object ReturnSignUpEvent:SecurityViewEvent()
    object ReturnResetPassEvent:SecurityViewEvent()
    object ReturnForgotPassEvent:SecurityViewEvent()
}