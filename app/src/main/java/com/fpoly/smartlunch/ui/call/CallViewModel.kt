package com.fpoly.smartlunch.ui.call

import android.util.Log
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.repository.ChatRepository
import com.fpoly.smartlunch.ui.call.call.WebRTCClient
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer

class CallViewModel @AssistedInject constructor(
    @Assisted state: CallViewState,
    private val repo: ChatRepository,
    private val webRTCClient: WebRTCClient
) : PolyBaseViewModel<CallViewState, CallViewAction, CallViewEvent>(state) {

    init {
        repo.connectSocket()
    }

    override fun handle(action: CallViewAction) {
        when(action){
            is CallViewAction.GetCurrentRoom -> getCurrentRoom(action.userId)
            is CallViewAction.GetLastCallMessage -> getLastCallMessage(action.roomId)
            is CallViewAction.postMessage -> postMessageCall(action.message)
            else ->{}
        }
    }

    private fun getCurrentRoom(userId: String?) {
        if (userId == null){
            setState { copy(curentRoom = Fail(Throwable())) }
            return
        }

        setState { copy(curentRoom = Loading()) }
        repo.getRoomWithUserId(userId).execute {
            copy(curentRoom = it)
        }
    }

    private fun getLastCallMessage(roomId: String?) {
        if (roomId == null){
            setState { copy(curentMessage = Fail(Throwable())) }
            return
        }

        setState { copy(curentMessage = Loading()) }
        repo.getLastCallMessage(roomId).execute {
            copy(curentMessage = it, answerMessage = it)
        }
    }

    private fun postMessageCall(message: Message){
        setState { copy(curentMessage = Loading()) }
        repo.postMessageCall(message).execute {
            copy(curentMessage = it, offerMessage = it)
        }
    }

    // lissten và send emit call video với socket
    fun connectEventCallSocket() {
        withState {
            val userId = it.curentRoom.invoke()?.userUserId?._id
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
    fun sendCallToServerSocket(requireCall: RequireCall){
        withState{
            requireCall.myUserId = it.curentRoom.invoke()?.userUserId?._id
            requireCall.targetUserId = it.curentRoom.invoke()?.shopUserId?._id
            repo.sendCallToSocket(requireCall)
        }
    }
    fun sendDataMessageCallToServerSocket(requireCall: RequireCall){
        withState{
            requireCall.myUserId = it.curentMessage.invoke()?._id
            requireCall.targetUserId = it.curentRoom.invoke()?.shopUserId?._id
            repo.sendCallToSocket(requireCall)
        }
    }

    // call video
    fun initObserverPeerConnection(observer: PeerConnection.Observer)
            = webRTCClient.observerPeerConnection(observer)

    fun initializeSurfaceView(surface: SurfaceViewRenderer) = webRTCClient.initializeSurfaceView(surface)
    fun startLocalVideo(localView: SurfaceViewRenderer) = webRTCClient.startLocalVideo(localView)
    fun callVideo(){
        webRTCClient.call{reqCall ->
            sendDataMessageCallToServerSocket(reqCall)
        }
    }
    fun answer() {
        webRTCClient.answer{reqCall ->
            sendDataMessageCallToServerSocket(reqCall)
        }
    }

    fun onRemoteSessionReceived(session: SessionDescription) = webRTCClient.onRemoteSessionReceived(session)
    fun addIceCandidate(p0: IceCandidate?) {
        webRTCClient.addIceCandidate(p0)
    }

    fun addViewToViewWebRTC(p0: MediaStream?){
        _viewEvents.post(CallViewEvent.addViewToViewWebRTC(p0))
    }

    fun initObserverPeerConnection(){
        _viewEvents.post(CallViewEvent.initObserverPeerConnection)
    }

    fun startCall(){
        webRTCClient.startCall()
    }
    fun endCall(){
        setState { copy(requireCall = null) }
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

    fun disconnectSocket(){ repo.disconnectSocket() }

    @AssistedFactory
    interface Factory {
        fun create(initialState: CallViewState): CallViewModel
    }

    companion object : MvRxViewModelFactory<CallViewModel, CallViewState> {
        override fun create(viewModelContext: ViewModelContext, state: CallViewState): CallViewModel? {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

}