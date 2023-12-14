package com.fpoly.smartlunch.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.ui.payment.PaymentViewEvent

sealed class UserViewEvent: PolyViewEvent {
    data class ReturnFragment<T: Fragment>(val fragmentClass: Class<T>, val bundle: Bundle? = null): UserViewEvent()
}