package com.fpoly.smartlunch.ui.chat.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.activityViewModel
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentSearchChatBinding
import com.fpoly.smartlunch.ui.chat.ChatViewmodel

class SearchChatFragment : PolyBaseFragment<FragmentSearchChatBinding>(){

    private val chatViewModel: ChatViewmodel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchChatBinding = FragmentSearchChatBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatViewModel.returnSetupAppbar(true, false,"search", false)
    }

    override fun invalidate() {

    }
}