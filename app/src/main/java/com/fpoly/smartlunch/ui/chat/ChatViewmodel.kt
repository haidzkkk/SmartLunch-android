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
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.network.SocketManager
import com.fpoly.smartlunch.data.repository.ChatRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class ChatViewmodel @AssistedInject constructor(
    @Assisted state: ChatViewState,
    private val repo: ChatRepository
) : PolyBaseViewModel<ChatViewState, ChatViewAction, ChatViewEvent>(state) {

    init {
        getRoomChat()
        repo.connectSocket()
    }
    override fun handle(action: ChatViewAction) {
        when(action){
            is ChatViewAction.getCurentUser -> getCurentUser()
            is ChatViewAction.getRoomChat -> getRoomChat()

            is ChatViewAction.setCurrentChat -> setCurentChat(action.room)
            is ChatViewAction.removeCurrentChat -> removeCurentChat()

            is ChatViewAction.postMessage -> postMessage(action.message)
            is ChatViewAction.removePostMessage -> removePostMessage()
            is ChatViewAction.returnOffEventMessageSocket -> repo.offReceiveMessage(action.roomId)
            is ChatViewAction.returnConnectSocket -> repo.connectSocket()
            is ChatViewAction.returnDisconnectSocket -> repo.disconnectSocket()
            else -> {}
        }
    }

    // lấy user hiện tại
    private fun getCurentUser() {
        setState { copy(curentUser = Loading()) }
        repo.getCurentUser().execute{
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

    // lấy thông tin phòng
    private fun setCurentChat(room: Room) {
        if (room.shopUserId == null || room._id == null){
            setState { copy(curentChatWithUser = Fail(Throwable()), curentMessage = Fail(Throwable())) }
            return
        }

        setState {copy(curentRoom = Success(room), curentChatWithUser = Success(room.userUserId!!), curentMessage = Loading()) }
        repo.getMessage(room._id).execute{
            copy(curentMessage = it)
        }

        // lắng nghe socket trả về khi có message mới được thêm
        repo.onReceiveMessage(room._id){
            Log.e("ChatViewmodel", "repo.onReceiveMessage: $it", )
            if (it != null){
                setState { copy(newMessage = Success(it)) }
            }else{
                setState { copy(newMessage = Fail(Throwable())) }
            }
        }
    }

    private fun removeCurentChat() {
        setState { copy(curentChatWithUser = Uninitialized, curentMessage = Uninitialized) }
    }

    // post message
    private fun postMessage(message: Message) {
        setState { copy(messageSent = Loading()) }
        repo.postMessage(message).execute{
            copy(messageSent = it)
        }
    }

    private fun removePostMessage() {
        setState { copy(messageSent = Uninitialized, newMessage = Uninitialized) }
    }

    fun returnSetupAppbar(isVisible: Boolean, isTextView: Boolean, tvTitle: String, isVisibleIconCall: Boolean){
        _viewEvents.post(ChatViewEvent.ReturnSetupAppbar(isVisible, isTextView, tvTitle, isVisibleIconCall))
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