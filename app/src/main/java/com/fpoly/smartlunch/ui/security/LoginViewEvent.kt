package com.fpoly.smartlunch.ui.security

import com.fpoly.smartlunch.core.PolyViewEvent

sealed class LoginViewEvent: PolyViewEvent {
    object ReturnFragmentViewEvent : LoginViewEvent()
    data class ToFragmentViewEvent(val id : Int) : LoginViewEvent()
}