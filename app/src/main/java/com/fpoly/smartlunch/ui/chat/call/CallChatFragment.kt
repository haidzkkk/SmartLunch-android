package com.fpoly.smartlunch.ui.chat.call

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.databinding.FragmentCallChatBinding
import com.fpoly.smartlunch.ui.chat.ChatViewEvent
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ultis.checkPermisionCallVideo
import com.fpoly.smartlunch.ultis.showSnackbar
import com.fpoly.smartlunch.ultis.startToDetailPermission
import org.webrtc.SessionDescription

class CallChatFragment : PolyBaseFragment<FragmentCallChatBinding>() {

    val chatViewmodel: ChatViewmodel by activityViewModel()

    private var isMute = true
    private var isCameraPause = true
    private val rtcAudioManager by lazy { RTCAudioManager.create(requireContext())}
    private var isSpeakerMode = true
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
        super.onDestroy()
    }

    private fun initUI() {
        views.layoytView.isVisible = false
        views.layoutRequireCall.isVisible = false

        var curentUser = withState(chatViewmodel) {it.curentUser.invoke()}
        var targetUser = withState(chatViewmodel) {it.curentCallWithUser}
        if (curentUser == null || targetUser == null) {
            Toast.makeText(requireContext(), "Không có thông tin của bạn", Toast.LENGTH_SHORT).show()
        }
        var displayName = "${curentUser!!.last_name} ${curentUser.first_name}"
        views.tvNameCalling.text = displayName
        views.tvNameLoading.text = displayName

        rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)

        initCamera()
    }

    private fun lisstenClickUI() {
        views.imgEndCall.setOnClickListener{
            chatViewmodel.sendCallToServer(RequireCall(RequireCallType.CREATE_STOP))
        }
        views.btnRefuse.setOnClickListener{
            chatViewmodel.sendCallToServer(RequireCall(RequireCallType.CREATE_STOP))
        }
        views.imgBack.setOnClickListener{
            chatViewmodel.sendCallToServer(RequireCall(RequireCallType.CREATE_STOP))
        }
        views.imgCamera.setOnClickListener{
            if (isCameraPause){
                isCameraPause = false
                views.imgCamera.setImageResource(R.drawable.icon_videocam_off)
            }else{
                isCameraPause = true
                views.imgCamera.setImageResource(R.drawable.baseline_videocam_24)
            }
            chatViewmodel.callToggleVideo(isCameraPause)
        }
        views.imgMic.setOnClickListener{
            if(isMute){
                isMute = false
                views.imgMic.setImageResource(R.drawable.icon_mic_off)
            }else{
                isMute = true
                views.imgMic.setImageResource(R.drawable.icon_mic)
            }
            chatViewmodel.callToggleAudio(isMute)
        }
        views.imgSwitchCamera.setOnClickListener{
            chatViewmodel.callSwichVideoCapture()
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

    private fun handleViewModel() {
        chatViewmodel.observeViewEvents {
            when(it){
                is ChatViewEvent.addViewToViewWebRTC -> {
                    Log.e("CallChatFragment", "addViewToViewWebRTC", )
                    it.p0?.videoTracks?.get(0)?.addSink(views.remoteView)
                }

                else ->{

                }
            }
        }
    }

    override fun invalidate(): Unit = withState(chatViewmodel) {
        when(it.requireCall?.type){
            RequireCallType.CALL_RESPONSE ->{
                if(it.requireCall.data == RequireCallType.OK){
                    views.layoytView.isVisible = true

                    chatViewmodel.startCall()
                    chatViewmodel.callVideo(it.curentUser.invoke()!!, it.curentCallWithUser!!)
                    chatViewmodel.setRequireCall(null)
                }else{
                    Toast.makeText(requireContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
                }
            }

            RequireCallType.OFFER_RECEIVED ->{
                if(it.requireCall.myUser != null){
                    chatViewmodel.setCallVideoWithUser(it.requireCall.myUser)
                }

                views.layoutControllCall.isVisible = false
                views.layoutRequireCall.isVisible = true
                views.tvNameLoading.text = "${it.requireCall.myUser?.last_name} ${it.requireCall.myUser?.first_name}"
                views.tvMessage.text = "${it.requireCall.myUser?.first_name} muốn gọi điện với bạn"

                views.btnAccept.setOnClickListener{v ->
                    views.layoutRequireCall.isVisible = false
                    views.layoutLoading.isVisible = false
                    views.layoytView.isVisible = true
                    views.layoutControllCall.isVisible = true

                    chatViewmodel.startCall()
                    var session = SessionDescription(SessionDescription.Type.OFFER, it.requireCall.data.toString()) //người trả lời nhận: sdp cua nguoi goi,
                    chatViewmodel.onRemoteSessionReceived(session)
                    chatViewmodel.answer(it.curentUser.invoke()!!, it.requireCall.myUser!!)
                }

                views.btnRefuse.setOnClickListener{
                    chatViewmodel.sendCallToServer(RequireCall(RequireCallType.CREATE_ANSWER, null, null, RequireCallType.NO))
                    chatViewmodel.setRequireCall(null)
                    requireActivity().onBackPressed()
                }
            }

            RequireCallType.STOP_RECEIVED ->{
                Toast.makeText(requireContext(), "Ok cút", Toast.LENGTH_SHORT).show()
                Log.e("CallChatFragment", "invalidate: RECEIVED_STOP", )
                views.localView.release()
                views.remoteView.release()
                chatViewmodel.endCall()
                findNavController().popBackStack()
            }

            RequireCallType.ANSWER_RECEIVED ->{
                if(it.requireCall.data.toString() != RequireCallType.NO){

                    val session = SessionDescription(SessionDescription.Type.ANSWER, it.requireCall.data.toString()) //người gọi nhận: sdp cua nguoi trả lời
                    chatViewmodel.onRemoteSessionReceived(session)

                    views.layoutLoading.isVisible = false
                }else{
                    Toast.makeText(requireContext(), "Nó từ chối mày", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun initCamera(){
        checkPermisionCallVideo{isAllow ->
            if (isAllow){
                chatViewmodel.initializeSurfaceView(views.localView)
                chatViewmodel.initializeSurfaceView(views.remoteView)
                chatViewmodel.startLocalVideo(views.localView)
            }else{
                showSnackbar(views.root, "Bạn phải cho quyền truy cập camera", true, "đi"){
                    requireActivity().startToDetailPermission()
                }
            }
        }
    }

}