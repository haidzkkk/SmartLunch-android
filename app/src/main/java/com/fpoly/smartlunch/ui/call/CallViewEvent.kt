package com.fpoly.smartlunch.ui.call

import com.fpoly.smartlunch.core.PolyViewEvent
import com.fpoly.smartlunch.ui.chat.ChatViewEvent
import org.webrtc.MediaStream

sealed class CallViewEvent: PolyViewEvent {
    object initObserverPeerConnection: CallViewEvent()
    data class addViewToViewWebRTC(val p0: MediaStream?): CallViewEvent()
}