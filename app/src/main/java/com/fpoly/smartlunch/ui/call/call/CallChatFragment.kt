package com.fpoly.smartlunch.ui.call.call

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.IceCandidateModel
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.MessageType
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.databinding.FragmentCallChatBinding
import com.fpoly.smartlunch.ui.call.CallViewAction
import com.fpoly.smartlunch.ui.call.CallViewEvent
import com.fpoly.smartlunch.ui.call.CallViewModel
import com.fpoly.smartlunch.ui.chat.ChatViewEvent
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ultis.checkPermisionCallVideo
import com.fpoly.smartlunch.ultis.showSnackbar
import com.fpoly.smartlunch.ultis.startToDetailPermission
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class CallChatFragment : PolyBaseFragment<FragmentCallChatBinding>() {

    private var currentState = 0
    private val stateAddMessage = 1
    private val stateCurrentMessage = 2

    val callViewModel: CallViewModel by activityViewModel()

    private var isMute = true
    private var isCameraPause = true
    private val rtcAudioManager by lazy { RTCAudioManager.create(requireContext()) }
    private var isSpeakerMode = true

    private val handler = Handler(Looper.getMainLooper())
    private val timeoutMillis: Long = 60000
    private val timeoutCallbackStopCall = Runnable {
        Toast.makeText(requireContext(), getString(R.string.end_call_offer), Toast.LENGTH_SHORT).show()
        callViewModel.sendDataMessageCallToServerSocket(RequireCall(RequireCallType.CREATE_STOP))
        activity?.finish()
    }
    private fun startTimeoutCallVideo() { handler.postDelayed(timeoutCallbackStopCall, timeoutMillis) }
    private fun cancelTimeoutCallVideo() { handler.removeCallbacks(timeoutCallbackStopCall) }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCallChatBinding {
        return FragmentCallChatBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        lisstenClickUI()
        handleViewModel()
    }

    override fun onDestroy() {
        views.localView.release()
        views.remoteView.release()
        callViewModel.endCall()
        cancelTimeoutCallVideo()
        super.onDestroy()
    }

    private fun initUI() {
        views.layoytView.isVisible = false
        rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
        initCamera()

    }

    private fun lisstenClickUI() {
        listenOnBackPress()
        views.imgEndCall.setOnClickListener{
            callViewModel.sendDataMessageCallToServerSocket(RequireCall(RequireCallType.CREATE_STOP))
            activity?.finish()
        }
        views.imgBack.setOnClickListener{
            callViewModel.sendDataMessageCallToServerSocket(RequireCall(RequireCallType.CREATE_STOP))
            activity?.finish()
        }
        views.imgCamera.setOnClickListener{
            if (isCameraPause){
                isCameraPause = false
                views.imgCamera.setImageResource(R.drawable.icon_videocam_off)
            }else{
                isCameraPause = true
                views.imgCamera.setImageResource(R.drawable.baseline_videocam_24)
            }
            callViewModel.callToggleVideo(isCameraPause)
        }
        views.imgMic.setOnClickListener{
            if(isMute){
                isMute = false
                views.imgMic.setImageResource(R.drawable.icon_mic_off)
            }else{
                isMute = true
                views.imgMic.setImageResource(R.drawable.icon_mic)
            }
            callViewModel.callToggleAudio(isMute)
        }
        views.imgSwitchCamera.setOnClickListener{
            callViewModel.callSwichVideoCapture()
        }
        views.imgVollumMode.setOnClickListener{
            if (isSpeakerMode){
                isSpeakerMode = false
                views.imgVollumMode.setImageResource(R.drawable.icon_vollum_mode_ear)
                rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.EARPIECE)
            }else{
                isSpeakerMode = true
                views.imgVollumMode.setImageResource(R.drawable.icon_vollum_mode_speak)
                rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
            }
        }
    }
    private fun listenOnBackPress() {
        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callViewModel.sendDataMessageCallToServerSocket(RequireCall(RequireCallType.CREATE_STOP))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callBack)
    }

    private fun handleViewModel() {
        callViewModel.observeViewEvents {
            when(it){
                is CallViewEvent.addViewToViewWebRTC -> {
                    Log.e("CallChatFragment", "addViewToViewWebRTC", )
                    it.p0?.videoTracks?.get(0)?.addSink(views.remoteView)
                }

                else ->{

                }
            }
        }
    }

    override fun invalidate(): Unit = withState(callViewModel) {
        when(it.curentRoom){
            is Success ->{
                var targetUser = it.curentRoom.invoke().shopUserId
                var displayName = "${targetUser!!.last_name} ${targetUser.first_name}"
                views.tvNameCalling.text = displayName
                views.tvNameLoading.text = displayName
            }
            else ->{

            }
        }

        when(it.curentMessage){
            is Success ->{
                views.layoytView.isVisible = true
                callViewModel.startCall()
                startTimeoutCallVideo()
            }
            is Fail ->{
                Toast.makeText(requireContext(), getString(R.string.call_faild), Toast.LENGTH_SHORT).show()
            }
            else ->{

            }
        }

        when(it.offerMessage){
            is Success ->{
                callViewModel.callVideo()
                it.offerMessage = Uninitialized
            }
            else ->{

            }
        }

        when(it.answerMessage){
            is Success ->{
                cancelTimeoutCallVideo()
                handleAddDataToCall(it.answerMessage.invoke())
                callViewModel.answer()
                views.layoutLoading.isVisible = false
                it.answerMessage = Uninitialized
            }
            else ->{

            }
        }

        when(it.requireCall?.type){
            RequireCallType.CALL_RESPONSE ->{
                it.requireCall = null
            }

            RequireCallType.ANSWER_RECEIVED ->{
                cancelTimeoutCallVideo()
                if(it.requireCall?.data.toString() != RequireCallType.NO){
                    val session = SessionDescription(SessionDescription.Type.ANSWER, it.requireCall?.data.toString()) //người gọi nhận: sdp cua nguoi trả lời
                    callViewModel.onRemoteSessionReceived(session)

                    views.layoutLoading.isVisible = false
                }else{
                    Toast.makeText(requireContext(), getString(R.string.refuse), Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
            }

            RequireCallType.STOP_RECEIVED ->{
                Toast.makeText(requireContext(), getString(R.string.end_call), Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    private fun handleAddDataToCall(message: Message?) {
        message?.sdp?.forEach{ sdpString ->
            val session = SessionDescription(SessionDescription.Type.OFFER, sdpString)
            callViewModel.onRemoteSessionReceived(session)
        }
        message?.iceCandidate?.forEach{ iceCandidateString ->
            val receivingIceCandidate = Gson().fromJson(iceCandidateString, IceCandidateModel::class.java)

            val iceCandidate = IceCandidate(
                receivingIceCandidate?.sdpMid ?: "",
                Math.toIntExact(receivingIceCandidate?.sdpMLineIndex?.toLong() ?: 0),
                receivingIceCandidate?.sdpCandidate ?: ""
            )

            callViewModel.addIceCandidate(iceCandidate)
        }
    }

    fun initCamera(){
        checkPermisionCallVideo{isAllow ->
            if (isAllow){
                callViewModel.initializeSurfaceView(views.localView)
                callViewModel.initializeSurfaceView(views.remoteView)
                callViewModel.startLocalVideo(views.localView)
            }else{
                showSnackbar(views.root, getString(R.string.access_camera), true, getString(R.string.ok)){
                    requireActivity().startToDetailPermission()
                }
            }
        }
    }

}