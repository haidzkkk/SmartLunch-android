package com.fpoly.smartlunch.ui.security

import com.fpoly.smartlunch.core.PolyViewAction

sealed class LoginViewAction: PolyViewAction {
    object GetUserAction: LoginViewAction()
    data class LoginAction(val userName: String, val password: String): LoginViewAction()
}