package com.fpoly.smartlunch.ui.paynow

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fpoly.smartlunch.core.PolyViewEvent

sealed class PayNowViewEvent : PolyViewEvent {
    data class ReturnFragment<T: Fragment>(val fragmentClass: Class<T>): PayNowViewEvent()
    data class ReturnFragmentWithArgument<T: Fragment>(val fragmentClass: Class<T>, val bundle: Bundle?): PayNowViewEvent()
    data class ReturnShowLoading(val isVisible: Boolean): PayNowViewEvent()
}