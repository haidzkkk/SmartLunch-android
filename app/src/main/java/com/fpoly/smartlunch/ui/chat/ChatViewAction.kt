package com.fpoly.smartlunch.ui.chat

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import java.io.File

sealed class ChatViewAction : PolyViewAction{

    object getCurentUser : ChatViewAction()
    object getRoomChat : ChatViewAction()

    data class setCurrentUserChat(val user: User): ChatViewAction()
    data class setCurrentChat(val roomId: String?): ChatViewAction()
    object removeCurrentChat: ChatViewAction()

    data class postMessage(val message: Message, val images: List<Gallery>? = null, val pathPhoto: String? = null): ChatViewAction()
    object removePostMessage: ChatViewAction()

    object returnConnectSocket: ChatViewAction()
    object returnDisconnectSocket: ChatViewAction()
    data class returnOffEventMessageSocket(val roomId: String): ChatViewAction()

    object getDataGallery: ChatViewAction()

    data class searchUserByName(val text: String): ChatViewAction()
    data class findRoomSearch(val userId: String?): ChatViewAction()
}