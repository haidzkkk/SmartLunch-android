package com.fpoly.smartlunch.ui.main.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fpoly.smartlunch.core.PolyViewEvent

sealed class PaymentViewEvent : PolyViewEvent {
    data class ReturnFragment<T: Fragment>(val fragmentClass: Class<T>): PaymentViewEvent()
    data class ReturnFragmentWithArgument<T: Fragment>(val fragmentClass: Class<T>, val bundle: Bundle): PaymentViewEvent()
}