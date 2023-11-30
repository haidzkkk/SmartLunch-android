package com.fpoly.smartlunch.ui.call

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.ui.chat.ChatViewAction

sealed class CallViewAction: PolyViewAction {
    data class GetCurrentRoom(val userId: String?): CallViewAction()
    data class GetLastCallMessage(val roomId: String?): CallViewAction()
    data class postMessage(val message: Message): CallViewAction()
}