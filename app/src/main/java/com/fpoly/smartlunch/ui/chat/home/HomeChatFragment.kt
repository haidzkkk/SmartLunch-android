package com.fpoly.smartlunch.ui.chat.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.core.PolyDialog
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.databinding.DialogOptionRoomBinding
import com.fpoly.smartlunch.databinding.FragmentHomeChatBinding
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewmodel

class HomeChatFragment : PolyBaseFragment<FragmentHomeChatBinding>() {

    private val roomChatAdapter: RoomChatAdapter by lazy { RoomChatAdapter(object : RoomChatAdapter.IOnClickRoomChatListenner{
        override fun onClickItem(room: Room) {
            findNavController().navigate(R.id.roomChatFragment)
            chatViewModel.handle(ChatViewAction.setCurrentChat(room))
        }

        override fun onLongClickItem(room: Room) {
            PolyDialog.Builder(requireContext(), DialogOptionRoomBinding.inflate(layoutInflater))
                .isTransparent(true).build().show()
        }
    }) }

    val chatViewModel: ChatViewmodel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRcv()
        clickUILisstenner()
        chatViewModel.returnSetupAppbar(true, true,"Đoạn chat", false)
    }

    private fun clickUILisstenner() {
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

    override fun invalidate() = withState(chatViewModel){
        when(it.rooms){
            is Success -> {
                roomChatAdapter.setData(it.rooms.invoke())
            }
            else -> {}
        }
    }


}