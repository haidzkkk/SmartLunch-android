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
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.repository.ChatRepository
import com.fpoly.smartlunch.ui.chat.call.WebRTCClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import java.io.File

class ChatViewmodel @AssistedInject constructor(
    @Assisted state: ChatViewState,
    private val repo: ChatRepository,
    private val webRTCClient: WebRTCClient
) : PolyBaseViewModel<ChatViewState, ChatViewAction, ChatViewEvent>(state) {

    init {
        repo.connectSocket()
        getCurentUser()
    }
    override fun handle(action: ChatViewAction) {
        when(action){
            is ChatViewAction.getCurentUser -> getCurentUser()
            is ChatViewAction.getRoomChat -> getRoomChat()

            is ChatViewAction.setCurrentChat -> setCurentChat(action.room)
            is ChatViewAction.removeCurrentChat -> removeCurentChat()

            is ChatViewAction.postMessage -> postMessage(action.message, action.files)
            is ChatViewAction.removePostMessage -> removePostMessage()
            is ChatViewAction.returnOffEventMessageSocket -> repo.offReceiveMessage(action.roomId)
            is ChatViewAction.returnConnectSocket -> repo.connectSocket()
            is ChatViewAction.returnDisconnectSocket -> repo.disconnectSocket()

            is ChatViewAction.getDataGallery -> getDataGallery()

            is ChatViewAction.searchUserByName -> searchUerByName(action.text)
            is ChatViewAction.findRoomSearch -> findRoomSearch(action.user)
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
    private fun setCurentChat(room: Room) {
        if (room._id == null){
            setState { copy(curentRoom = Fail(Throwable()), curentMessage = Fail(Throwable())) }
            return
        }

        setState {copy(curentRoom = Loading(), curentMessage = Loading()) }

        repo.getRoomById(room._id).execute{
            copy(curentRoom = it)
        }

        repo.getMessage(room._id).execute{
            copy(curentMessage = it)
        }

        repo.onReceiveMessage(room._id){
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
    private fun postMessage(message: Message, files: List<File>?) {
        setState { copy(messageSent = Loading()) }
        repo.postMessage(message, files).execute{
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

    private fun findRoomSearch(user: User){
        repo.getRoomWithUserId(user._id).execute {
            Log.e("ChatViewModel", "findRoomSearch: room : ${it.invoke()}", )
            if (it.invoke()?._id != null){
                setCurentChat(it.invoke()!!)
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
            requireCall.myUser = it.curentUser.invoke()
            requireCall.targetUser = it.curentCallWithUser ?: it.curentRoom.invoke()?.shopUserId
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

    // call video
    fun initObserverPeerConnection(observer: PeerConnection.Observer)
    = webRTCClient.observerPeerConnection(observer)

    fun initializeSurfaceView(surface: SurfaceViewRenderer) = webRTCClient.initializeSurfaceView(surface)
    fun startLocalVideo(localView: SurfaceViewRenderer) = webRTCClient.startLocalVideo(localView)
    fun callVideo(myUser: User, targetUser: User){
        webRTCClient.call(myUser, targetUser){
            repo.sendCallToSocket(it)
        }
    }
    fun onRemoteSessionReceived(session: SessionDescription) = webRTCClient.onRemoteSessionReceived(session)
    fun answer(myUser: User, targetUser: User) {
        webRTCClient.answer(myUser, targetUser){
            repo.sendCallToSocket(it)
        }
    }

    fun addIceCandidate(p0: IceCandidate?) {
        webRTCClient.addIceCandidate(p0)
    }

    fun addViewToViewWebRTC(p0: MediaStream?){
        _viewEvents.post(ChatViewEvent.addViewToViewWebRTC(p0))
    }

    fun initObserverPeerConnection(){
        _viewEvents.post(ChatViewEvent.initObserverPeerConnection)
    }

    fun startCall(){
        webRTCClient.startCall()
    }
    fun endCall(){
        setState { copy(requireCall = null, requireCallIceCandidate = null, curentCallWithUser = null) }
        webRTCClient.endCall()
    }

    fun callSwichVideoCapture(){
        webRTCClient.swichVideoCapture()
    }

    fun callToggleVideo(isCameraPause: Boolean){
        webRTCClient.toggleVideo(isCameraPause)
    }

    fun callToggleAudio(isMute: Boolean){
        webRTCClient.toggleAudio(isMute)
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