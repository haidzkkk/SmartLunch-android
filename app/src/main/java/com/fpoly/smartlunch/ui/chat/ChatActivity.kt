package com.fpoly.smartlunch.ui.chat

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.core.example.ChatViewState
import com.fpoly.smartlunch.data.model.IceCandidateModel
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.databinding.ActivityChatBinding
import com.fpoly.smartlunch.ui.call.CallActivity
import com.fpoly.smartlunch.ui.call.call.MyPeerConnectionObserver
import com.fpoly.smartlunch.ui.call.call.WebRTCClient
import com.fpoly.smartlunch.ui.notification.receiver.MyReceiver
import com.fpoly.smartlunch.ultis.MyConfigNotifi
import com.fpoly.smartlunch.ultis.startActivityWithData
import com.google.gson.Gson
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.SessionDescription
import javax.inject.Inject
import kotlin.math.log

@SuppressLint("UnspecifiedRegisterReceiverFlag")
class ChatActivity : PolyBaseActivity<ActivityChatBinding>(), ChatViewmodel.Factory{

    val intentFilterCall = IntentFilter(MyReceiver.actionCall)

    private lateinit var navController: NavController
    private val chatViewmodel: ChatViewmodel by viewModel()

    private val broadcastReceiverCall = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent?.extras?.getString("type")
            val idUrl = intent?.extras?.getString("idUrl")

            val currentFragmentDstiantion = navController.currentDestination
            if (currentFragmentDstiantion?.id != R.id.roomChatFragment) {
                navController.navigate(R.id.roomChatFragment)
            }
            chatViewmodel.handle(ChatViewAction.findRoomSearch(idUrl))

            val intent = Intent(context, CallActivity::class.java)
            startActivityWithData(intent, type, idUrl)
        }
    }

    @Inject
    lateinit var chatViewmodelFactory: ChatViewmodel.Factory

    override fun getBinding(): ActivityChatBinding = ActivityChatBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyComponent.inject(this)
        super.onCreate(savedInstanceState)

        initUI()
        lisstenClickUI()
        handleReciveDataNotifi()

    }
    private fun initUI() {
        navController= findNavController(R.id.nav_fragment)
    }

    private fun handleReciveDataNotifi() {
        val type = intent.extras?.getString("type")
        val userId = intent.extras?.getString("idUrl")

        when(type){
            MyConfigNotifi.TYPE_CHAT ->{
                navController.navigate(R.id.roomChatFragment)
                chatViewmodel.handle(ChatViewAction.findRoomSearch(userId))
            }
            MyConfigNotifi.TYPE_CALL_OFFER ->{
                navController.navigate(R.id.roomChatFragment)
                chatViewmodel.handle(ChatViewAction.findRoomSearch(userId))

                val intent = Intent(this, CallActivity::class.java)
                startActivityWithData(intent, type, userId)
            }
            MyConfigNotifi.TYPE_CALL_ANSWER ->{
                navController.navigate(R.id.roomChatFragment)
                chatViewmodel.handle(ChatViewAction.findRoomSearch(userId))

                val intent = Intent(this, CallActivity::class.java)
                startActivityWithData(intent, type, userId)
            }
        }
    }

    private fun lisstenClickUI() {

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiverCall, intentFilterCall)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiverCall)
    }

    override fun onDestroy() {
        chatViewmodel.handle(ChatViewAction.returnDisconnectSocket)
        super.onDestroy()
    }

    override fun create(initialState: ChatViewState): ChatViewmodel {
        return chatViewmodelFactory.create(initialState)
    }

}
