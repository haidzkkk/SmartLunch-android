package com.fpoly.smartlunch.ui.call

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User

data class CallViewState(
    val curentRoom: Async<Room> = Uninitialized,
    val curentMessage: Async<Message> = Uninitialized,

    var offerMessage: Async<Message> = Uninitialized,
    var answerMessage: Async<Message> = Uninitialized,

    var requireCall: RequireCall? = null,
    var requireCallIceCandidate: RequireCall? = null,
): MvRxState {
}