package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.network.AuthApi
import com.fpoly.smartlunch.data.network.ChatApi
import com.fpoly.smartlunch.data.network.ContentDataSource
import com.fpoly.smartlunch.data.network.SocketManager
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatApi: ChatApi,
    private val authApi: AuthApi,
    private val socketManager: SocketManager,
    private val contentDataSource: ContentDataSource
) {
    fun getUserById(id: String): Observable<User> = authApi.getUserById(id).subscribeOn(Schedulers.io())
    fun getCurentUser(): Observable<User> = authApi.getCurrentUser().subscribeOn(Schedulers.io())

    fun getRoom(): Observable<ArrayList<Room>> = chatApi.getRoom().subscribeOn(Schedulers.io())
    fun getRoomById(roomId: String): Observable<Room> = chatApi.getRoomById(roomId).subscribeOn(Schedulers.io())
    fun getRoomWithUserId(withUserId: String): Observable<Room> = chatApi.getRoomWithUserId(withUserId).subscribeOn(Schedulers.io())
    fun getMessage(idRoom: String): Observable<ArrayList<Message>> = chatApi.getMessage(idRoom).subscribeOn(Schedulers.io())

    fun postMessage(message: Message, images: List<Gallery>?): Observable<Message>{
        if (message.roomId == null) return Observable.just(null)

        val reqBodyRoomId: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), message.roomId)
        val reqBodyMessage: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), message.message ?: "")
        val reqBodyLinkMessage: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), message.linkMessage ?: "")
        val reqBodyType: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), (message.type ?: 0).toString())
        val reqBodyImages: ArrayList<MultipartBody.Part> = ArrayList()

        if (images != null){
            val files = images.map { File(it.realPath) }.toList()
            for (file in files){
                if (file != null){
                    val reqBodyImage: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    val multipartBodyImage: MultipartBody.Part = MultipartBody.Part.createFormData("images", file.name, reqBodyImage)
                    reqBodyImages.add(multipartBodyImage)
                }
            }
        }

        return chatApi.postMessage(reqBodyRoomId, reqBodyMessage, reqBodyLinkMessage, reqBodyType, reqBodyImages).subscribeOn(Schedulers.io())
    }
    // socket
    fun connectSocket(){ socketManager.connect() }
    fun disconnectSocket(){ socketManager.disconnect() }

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


    // content provider
    suspend fun getImageFromGallery(): ArrayList<Gallery> {
        return withContext(Dispatchers.IO){
            contentDataSource.getImage()
        }
    }

    suspend fun getDataFromGallery(): ArrayList<Gallery> {
        return withContext(Dispatchers.IO){
            contentDataSource.getImageAndVideo()
        }
    }

    fun searchUser(text: String): Observable<ArrayList<User>> = authApi.searchUserByName(text).subscribeOn(Schedulers.io())


    // chat video
    fun onReceiveCall(userId: String, callBack: (requireCall: RequireCall?) -> Unit){
        socketManager.onReceiveEmitSocket(SocketManager.CLIENT_LISTEN_CALL + userId, RequireCall::class.java){
            callBack(it)
        }
    }

    fun sendCallToSocket(data: RequireCall){
        socketManager.sendEmitToSocket(SocketManager.SERVER_LISTEN_CALL, data)
    }


}