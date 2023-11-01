package com.fpoly.smartlunch.ui.chat

import com.fpoly.smartlunch.core.PolyViewEvent
import org.webrtc.MediaStream

sealed class ChatViewEvent : PolyViewEvent{
    object initObserverPeerConnection: ChatViewEvent()
    data class addViewToViewWebRTC(val p0: MediaStream?): ChatViewEvent()
}