package com.fpoly.smartlunch.data.network

import android.util.Log
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Objects
import javax.inject.Inject

class SocketManager @Inject constructor() {

    companion object {
        const val CLIENT_LISTEN_ROOM: String = "client-listen-room-"
        const val CLIENT_LISTEN_MESSAGE: String = "client-listen-message-"
    }

    private var mSocket: Socket? = null
    val socket get () = mSocket

    init {
        this.mSocket = IO.socket(RemoteDataSource.BASE_URL.replace("3000", "3001"))
        Log.e("TAG", "SocketManager: init ${SocketManager.hashCode()}, socker: ${mSocket.hashCode()}", )
    }

    public fun connect(){
        Log.e("SocketManager", "connect: connect  ${mSocket.hashCode()}", )
        mSocket?.connect()
    }

    public fun disconnect(){
        Log.e("SocketManager", "connect: disconnect  ${mSocket.hashCode()}", )
        mSocket?.disconnect()
    }


    public fun sendEmitSocket(event: String, data: Any){
        if (mSocket?.connected() == true){
            mSocket?.emit(event, Gson().toJson(data))
        }else{
            Log.e("SocketManager", "socket: isDisconnect", )
        }
    }

    public fun <T> onReceiveEmitSocket(event: String, type: Class<T>, callBack: (data: T?) -> Unit){
        if (mSocket?.connected() == true){
            Log.e("SocketManager", "socket: onReceiveEmitSocket ok ${mSocket.hashCode()}", )
            mSocket!!.on(event){
                if (!it[0].toString().isNullOrEmpty()){
                    CoroutineScope(Dispatchers.Main).launch {
                        callBack(Gson().fromJson(it[0].toString(), type))
                    }
                }else{
                    Log.e("SocketManager", "receive: event $event -> null", )
                }
            }
        }else{
            Log.e("SocketManager", "socket: onReceiveEmitSocket isDisconnect  ${mSocket.hashCode()}", )
        }
    }

    public fun offReceiEmitSocket(event: String) {
        if (mSocket?.connected() == true){
            mSocket!!.off(event)
        }
    }
}