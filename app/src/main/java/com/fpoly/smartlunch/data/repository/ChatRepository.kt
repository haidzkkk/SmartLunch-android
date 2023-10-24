package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.network.AuthApi
import com.fpoly.smartlunch.data.network.ChatApi
import com.fpoly.smartlunch.data.network.SocketManager
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatApi: ChatApi,
    private val authApi: AuthApi,
    private val socketManager: SocketManager,
) {


    fun getUserById(id: String): Observable<User> = authApi.getUserById(id).subscribeOn(Schedulers.io())
    fun getCurentUser(): Observable<User> = authApi.getCurrentUser().subscribeOn(Schedulers.io())

    fun getRoom(): Observable<ArrayList<Room>> = chatApi.getRoom().subscribeOn(Schedulers.io())
    fun getMessage(idRoom: String): Observable<ArrayList<Message>> = chatApi.getMessage(idRoom).subscribeOn(Schedulers.io())
    fun postMessage(message: Message): Observable<Message> = chatApi.postMessage(message).subscribeOn(Schedulers.io())

    // socket
    fun connectSocket(){ socketManager.connect() }
    fun disconnectSocket(){ socketManager.disconnect() }

    fun sendMessageSocket(){}

    fun onReceiveMessage(roomId: String, callBack: (message: Message?) -> Unit){
        socketManager.onReceiveEmitSocket(SocketManager.CLIENT_LISTEN_MESSAGE + roomId, Message::class.java){
            callBack(it)
        }
    }
    fun onReceiveRoom(userId: String, callBack: (room: Room?) -> Unit){
        socketManager.onReceiveEmitSocket(SocketManager.CLIENT_LISTEN_ROOM + userId, Room::class.java){
            callBack(it)
        }
    }
    fun offReceiveMessage(roomId: String){
        socketManager.offReceiEmitSocket(SocketManager.CLIENT_LISTEN_MESSAGE + roomId)
    }
    fun offReceiveRoom(userId: String){
        socketManager.offReceiEmitSocket(SocketManager.CLIENT_LISTEN_ROOM + userId)
    }

}