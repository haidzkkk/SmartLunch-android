package com.fpoly.smartlunch.core.example

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User

data class ChatViewState(
    val curentUser: Async<User> = Uninitialized,
    val rooms: Async<ArrayList<Room>> = Uninitialized,

    val curentRoom: Async<Room> = Uninitialized,
    val curentMessage: Async<ArrayList<Message>> = Uninitialized,
    val messageSent: Async<Message> = Uninitialized,
    val galleries: Async<ArrayList<Gallery>> = Uninitialized,

    val newMessage: Async<Message> = Uninitialized,                    // Message được socket giử về

    val curentUsersSreach: Async<ArrayList<User>> = Uninitialized,

    val requireCall: RequireCall? = null,
    val requireCallIceCandidate: RequireCall? = null,
    val curentCallWithUser: User? = null,
): MvRxState

