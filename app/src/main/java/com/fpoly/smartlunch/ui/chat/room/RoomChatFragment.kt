package com.fpoly.smartlunch.ui.chat.room

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.MessageType
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.databinding.FragmentRoomChatBinding
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ultis.checkNull
import com.fpoly.smartlunch.ultis.showSnackbar


class RoomChatFragment : PolyBaseFragment<FragmentRoomChatBinding>() {

    var adapter: RoomChatAdapter? = null
    val chatViewmodel: ChatViewmodel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRoomChatBinding {
        return FragmentRoomChatBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRcv()
        listenClickUi()
    }

    private fun setupRcv() {
        var user: User? = withState(chatViewmodel){ it.curentUser.invoke() }
        if (user != null){
            adapter = RoomChatAdapter(user)
        }

        if (adapter != null){
            views.rcvChat.adapter = adapter
            views.rcvChat.layoutManager = LinearLayoutManager(requireContext())
        }else{
            showSnackbar(views.root, "Không có curent user", false, "Quay lại"){}
        }

    }

    private fun listenClickUi() {
        views.edtMessage.setOnClickListener{
            views.rcvChat.scrollToPosition((withState(chatViewmodel){it.curentMessage.invoke()}?.size ?: 0) - 1)
        }

        views.imgSend.setOnClickListener{
            handleSendMessage()
        }
    }

    private fun handleSendMessage() {
        var roomId = withState(chatViewmodel){it.curentRoom.invoke()}
        if (views.edtMessage.text.toString() != null && roomId != null){
            Log.e("TAG", "handleSendMessage: ${views.edtMessage.text.toString()}", )
            var message = Message(null, roomId._id, null, message = views.edtMessage.text.toString(), null, null, MessageType.TYPE_TEXT, null)
            chatViewmodel.handle(ChatViewAction.postMessage(message))

            views.edtMessage.setText("")
        }
    }

    override fun onDestroyView() {
        chatViewmodel.handle(ChatViewAction.returnOffEventMessageSocket(withState(chatViewmodel){ it.curentRoom.invoke()?._id ?: ""}))
        chatViewmodel.handle(ChatViewAction.removeCurrentChat)
        super.onDestroyView()
    }

    override fun invalidate(): Unit = withState(chatViewmodel){
        when(it.curentChatWithUser){
            is Success -> {
                Log.e("TAG", "handleAppbar: hello0", )
                chatViewmodel.returnSetupAppbar(true, true, it.curentChatWithUser.invoke().first_name, true)
            }

            is Fail ->{
                showSnackbar(views.root, "Không có your user", false, "Quay lại"){
                }
            }
            is Loading ->{

            }
            else ->{
            }
        }

        when(it.curentMessage){
            is Success -> {
                if (adapter != null){
                    adapter!!.setData(it.curentMessage.invoke())
                    views.rcvChat.scrollToPosition(it.curentMessage.invoke().size - 1)
                }
            }
            is Fail ->{

            }
            is Loading ->{

            }
            else ->{
            }
        }

        when(it.messageSent){
            is Success -> {
                if (adapter != null){
//                    adapter!!.addData(it.messageSent.invoke())
//                    views.rcvChat.scrollToPosition((it.curentMessage.invoke()?.size ?: 0) - 1)
                }
            }
            is Fail ->{

            }
            is Loading ->{

            }
            else ->{
            }
        }

        when(it.newMessage){
            is Success -> {
                if (adapter != null
//                    && !it.curentUser.invoke()?._id.isNullOrEmpty() && !it.newMessage.invoke().userIdSend?._id.isNullOrEmpty()
//                    && it.curentUser.invoke()!!._id != it.newMessage.invoke().userIdSend!!._id
                    ){
                    adapter!!.addData(it.newMessage.invoke())
                    views.rcvChat.scrollToPosition((it.curentMessage.invoke()?.size ?: 0) - 1)
                    chatViewmodel.handle(ChatViewAction.removePostMessage)
                }else{
                    Log.e("RoomChatFragment", "newMessage: socket receive faild", )
                }
            }
            is Fail ->{

            }
            is Loading ->{

            }
            else ->{

            }
        }

    }

}