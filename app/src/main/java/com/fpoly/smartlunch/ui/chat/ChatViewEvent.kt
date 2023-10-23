package com.fpoly.smartlunch.ui.chat

import com.fpoly.smartlunch.core.PolyViewEvent

sealed class ChatViewEvent : PolyViewEvent{
    data class ReturnSetupAppbar(val isVisible: Boolean, val isTextView: Boolean, val tvTitle: String, val isVisibleIconCall: Boolean): ChatViewEvent()
}