package com.fpoly.smartlunch.ui.call

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.core.example.ChatViewState
import com.fpoly.smartlunch.data.model.IceCandidateModel
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.MessageType
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.databinding.ActivityCallBinding
import com.fpoly.smartlunch.ui.call.call.CallChatFragment
import com.fpoly.smartlunch.ui.call.call.MyPeerConnectionObserver
import com.fpoly.smartlunch.ui.call.waiting.WaitingFragment
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ultis.MyConfigNotifi
import com.fpoly.smartlunch.ultis.commitTransaction
import com.google.gson.Gson
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import javax.inject.Inject

class CallActivity : PolyBaseActivity<ActivityCallBinding>(), CallViewModel.Factory {

    private var currentState = 0
    private val stateCurrentRoom = 1

    private val callViewModel: CallViewModel by viewModel()

    private var typeCall: String? = null


    @Inject
    lateinit var callViewModelFactory: CallViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        initUI()
        handleReciveData()
        handleViewModel()
    }

    override fun onDestroy() {
        callViewModel.disconnectSocket()
        super.onDestroy()
    }
    private fun initUI() {
        callViewModel.initObserverPeerConnection(object: MyPeerConnectionObserver(){
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)

                callViewModel.addIceCandidate(p0)
                val candidate = hashMapOf(
                    "sdpMid" to p0?.sdpMid,
                    "sdpMLineIndex" to p0?.sdpMLineIndex,
                    "sdpCandidate" to p0?.sdp
                )

                callViewModel.sendDataMessageCallToServerSocket(RequireCall(RequireCallType.ICE_CANDIDATE, null, null, candidate))
            }

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                callViewModel.addViewToViewWebRTC(p0)
            }
        })
    }

    private fun handleReciveData() {
        typeCall = intent.extras?.getString("type")
        val userId = intent.extras?.getString("idUrl")
        when(typeCall){
            MyConfigNotifi.TYPE_CALL_OFFER ->{
                supportFragmentManager.commitTransaction {
                    replace(R.id.frame_layout, CallChatFragment())
                }
            }
            MyConfigNotifi.TYPE_CALL_ANSWER ->{
                supportFragmentManager.commitTransaction {
                    replace(R.id.frame_layout, WaitingFragment())
                }
            }
            else -> {
                Toast.makeText(this, "Không biet type bạn đang làm gì", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        currentState = stateCurrentRoom
        callViewModel.handle(CallViewAction.GetCurrentRoom(userId))
    }

    private fun handleViewModel() {

        callViewModel.subscribe(this){
            when(it.curentRoom) {
                is Success ->{
                    if (currentState == stateCurrentRoom){
                        callViewModel.connectEventCallSocket()

                        if (typeCall == MyConfigNotifi.TYPE_CALL_OFFER){
                            val message = Message(null, it.curentRoom.invoke()._id, null,
                                "Cuộc gọi", null, null, MessageType.TYPE_CALLING,
                                arrayListOf(), arrayListOf(), null, null)
                            callViewModel.handle(CallViewAction.postMessage(message))

                        }else if(typeCall == MyConfigNotifi.TYPE_CALL_ANSWER){
                            callViewModel.handle(CallViewAction.GetLastCallMessage(it.curentRoom.invoke()._id))

                        }
                    }
                    currentState = 0
                }
                is Fail ->{
                    Toast.makeText(this, "Không tìm thấy phòng", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else ->{

                }
            }
            if (it.requireCallIceCandidate != null){
                var receivingIceCandidate = Gson().fromJson(it.requireCallIceCandidate?.data.toString(), IceCandidateModel::class.java)

                var iceCandidate = IceCandidate(
                    receivingIceCandidate?.sdpMid ?: "",
                    Math.toIntExact(receivingIceCandidate?.sdpMLineIndex?.toLong() ?: 0),
                    receivingIceCandidate?.sdpCandidate ?: ""
                )

                callViewModel.addIceCandidate(iceCandidate)
            }
        }
    }

    override fun getBinding(): ActivityCallBinding {
        return ActivityCallBinding.inflate(layoutInflater)
    }

    override fun create(initialState: CallViewState): CallViewModel {
        return callViewModelFactory.create(initialState)
    }
}