package com.fpoly.smartlunch.ui.chat

import android.util.Log
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.core.example.ChatViewState
import com.fpoly.smartlunch.data.model.Gallery
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.repository.ChatRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ChatViewmodel @AssistedInject constructor(
    @Assisted state: ChatViewState,
    private val repo: ChatRepository
) : PolyBaseViewModel<ChatViewState, ChatViewAction, ChatViewEvent>(state) {

    init {
        repo.connectSocket()
        getCurentUser()
    }
    override fun handle(action: ChatViewAction) {
        when(action){
            is ChatViewAction.getCurentUser -> getCurentUser()
            is ChatViewAction.getRoomChat -> getRoomChat()

            is ChatViewAction.setCurrentChat -> setCurentChat(action.roomId)
            is ChatViewAction.removeCurrentChat -> removeCurentChat()

            is ChatViewAction.postMessage -> postMessage(action.message, action.images, action.pathPhoto)
            is ChatViewAction.removePostMessage -> removePostMessage()
            is ChatViewAction.returnOffEventMessageSocket -> repo.offReceiveMessage(action.roomId)
            is ChatViewAction.returnConnectSocket -> repo.connectSocket()
            is ChatViewAction.returnDisconnectSocket -> repo.disconnectSocket()

            is ChatViewAction.getDataGallery -> getDataGallery()

            is ChatViewAction.searchUserByName -> searchUerByName(action.text)
            is ChatViewAction.findRoomSearch -> findRoomSearch(action.userId)
            else -> {}
        }
    }

    // lấy user hiện tại
    private fun getCurentUser() {
        setState { copy(curentUser = Loading()) }
        repo.getCurentUser().execute{
            getRoomChat()
            copy(curentUser = it)
        }
    }

    // lấy danh sách phòng
    private fun getRoomChat() {
        setState { copy(rooms = Loading()) }
        repo.getRoom().execute{
            copy(rooms = it)
        }
    }

    // setup thông tin phòng
    private fun setCurentChat(roomId: String?) {
        if (roomId == null){
            setState { copy(curentRoom = Fail(Throwable()), curentMessage = Fail(Throwable())) }
            return
        }

        setState {copy(curentRoom = Loading(), curentMessage = Loading()) }

        repo.getRoomById(roomId).execute{
            copy(curentRoom = it)
        }

        repo.getMessage(roomId).execute{
            copy(curentMessage = it)
        }

        repo.onReceiveMessage(roomId){
            if (it != null){
                setState { copy(newMessage = Success(it)) }
            }else{
                setState { copy(newMessage = Fail(Throwable())) }
            }
        }
    }

    private fun removeCurentChat() {
        setState { copy(curentRoom = Uninitialized, curentMessage = Uninitialized, messageSent = Uninitialized) }
    }

    // post message
    private fun postMessage(message: Message, images: List<Gallery>?, pathPhoto: String?) {
        setState { copy(messageSent = Loading()) }
        repo.postMessage(message, images, pathPhoto).execute{
            copy(messageSent = it)
        }
    }

    fun connectEventRoomSocket(callBack: (room: Room?) -> Unit) {
        withState {
            var userId = it.curentUser.invoke()?._id
            if (!userId.isNullOrEmpty()){
                repo.onReceiveRoom(userId, callBack)
            }
        }
    }

    private fun removePostMessage() {
        setState { copy(messageSent = Uninitialized, newMessage = Uninitialized) }
    }

    // gallery
    private fun getDataGallery() {
        setState { copy(galleries = Loading()) }
        CoroutineScope(Dispatchers.Main).launch {
            val data = repo.getDataFromGallery()
            val sortData= data.sortedByDescending { it.date }.toCollection(ArrayList())
            setState { copy(galleries =  Success(sortData)) }
        }
    }

    // search user
    private fun searchUerByName(text: String) {
        setState { copy(curentUsersSreach = Loading()) }
        repo.searchUser(text).execute {
            copy(curentUsersSreach = it)
        }
    }

    private fun findRoomSearch(userId: String?){
        if (userId == null){
            setState { copy(curentRoom = Fail(Throwable()), curentMessage = Fail(Throwable())) }
            return
        }
        repo.getRoomWithUserId(userId).execute {
            if (it is Success){
                setCurentChat(it.invoke()._id)
            }
            copy()
        }
    }


    // lissten và send emit call video với socket
    fun connectEventCallSocket() {
        withState {
            var userId = it.curentUser.invoke()?._id
            if (!userId.isNullOrEmpty()){
                repo.onReceiveCall(userId){ requireCall ->
                    if(requireCall?.type == RequireCallType.ICE_CANDIDATE){
                        setState { copy(requireCallIceCandidate = requireCall) }
                    }else{
                        setState { copy(requireCall = requireCall) }
                    }
                }
            }
        }
    }
    fun sendCallToServer(requireCall: RequireCall){
        withState{
            requireCall.myUserId = it.curentUser.invoke()?._id
            requireCall.targetUserId = it.curentCallWithUser?._id ?: it.curentRoom.invoke()?.shopUserId?._id
            Log.e("ChatViewModel", "RequireCall: ${requireCall}", )
            repo.sendCallToSocket(requireCall)
        }
    }

    fun setRequireCall(requireCall: RequireCall?){
        setState { copy(requireCall = requireCall) }
    }

    fun setCallVideoWithUser(callWithUser: User?){
        setState { copy(curentCallWithUser = callWithUser) }
    }
    @AssistedFactory
    interface Factory {
        fun create(initialState: ChatViewState): ChatViewmodel
    }

    companion object : MvRxViewModelFactory<ChatViewmodel, ChatViewState> {
        override fun create(viewModelContext: ViewModelContext, state: ChatViewState): ChatViewmodel? {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}