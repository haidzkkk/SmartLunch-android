package com.fpoly.smartlunch.ui.chat.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.core.PolyDialog
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.databinding.DialogOptionRoomBinding
import com.fpoly.smartlunch.databinding.FragmentHomeChatBinding
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ui.call.call.WebRTCClient
import javax.inject.Inject

class HomeChatFragment : PolyBaseFragment<FragmentHomeChatBinding>() {

    private val roomChatAdapter: RoomChatAdapter by lazy { RoomChatAdapter(object : RoomChatAdapter.IOnClickRoomChatListenner{
        override fun onClickItem(room: Room) {
            findNavController().navigate(R.id.roomChatFragment)
            chatViewModel.handle(ChatViewAction.setCurrentChat(room._id))
        }

        override fun onLongClickItem(room: Room) {
            PolyDialog.Builder(requireContext(), DialogOptionRoomBinding.inflate(layoutInflater))
                .isTransparent(true).build().show()
        }
    }) }

    val chatViewModel: ChatViewmodel by activityViewModel()

    @Inject
    lateinit var webRTCClient: WebRTCClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)

        initUI()
        setupRcv()
        clickUILisstenner()
        Log.e("HomeChatFragment", "onCreate: webRTCClient ${webRTCClient.hashCode()}", )
    }

    private fun initUI() {
        views.layoutHeader.imgBack.isVisible = true
        views.layoutHeader.tvTitleToolbar.text = "Đoạn chat"
    }

    private fun clickUILisstenner() {
        views.layoutHeader.imgBack.setOnClickListener{
//            activity?.onBackPressed()
            activity?.finish()
        }

        views.edtSearch.setOnClickListener{
            findNavController().navigate(R.id.searchChatFragment)
        }
    }

    private fun setupRcv() {
        views.rcvChat.addItemDecoration(DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL))
        views.rcvChat.layoutManager = LinearLayoutManager(requireContext())
        views.rcvChat.adapter = roomChatAdapter;
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeChatBinding = FragmentHomeChatBinding.inflate(layoutInflater)

    override fun invalidate(): Unit = withState(chatViewModel){
        when(it.curentUser){
            is Success -> {
                chatViewModel.connectEventRoomSocket{ roomChatAdapter.updateData(it)} // socket receive room update
                chatViewModel.connectEventCallSocket()
            }
            else -> {}
        }
        when(it.rooms){
            is Success -> {
                roomChatAdapter.setData(it.rooms.invoke())
            }
            else -> {}
        }
    }
}