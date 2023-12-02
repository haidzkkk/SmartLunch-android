package com.fpoly.smartlunch.ui.chat.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentSearchChatBinding
import com.fpoly.smartlunch.ui.chat.ChatViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewmodel
import com.fpoly.smartlunch.ultis.showKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchChatFragment : PolyBaseFragment<FragmentSearchChatBinding>(){
    var myDelayJob: Job? = null

    lateinit var adapter: SearchChatAdapter

    private val chatViewModel: ChatViewmodel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchChatBinding = FragmentSearchChatBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        lisstenUI()

    }

    private fun initUI() {
        context?.showKeyboard(views.edtTitle)

        adapter = SearchChatAdapter{
            findNavController().navigate(R.id.roomChatFragment)
            chatViewModel.handle(ChatViewAction.findRoomSearch(it._id))
        }
        views.rcv.adapter = adapter
        views.rcv.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun lisstenUI() {
        views.imgBack.setOnClickListener{
            activity?.onBackPressed()
        }

        views.imgClear.setOnClickListener{
            views.edtTitle.setText("")
        }

        views.edtTitle.doOnTextChanged { text, start, before, count ->
            views.imgClear.isVisible = text.toString().length != 0

            if (text.toString().length == 0){
                views.tvFinding.text = "Hãy tìm kiếm"
            }else{
                views.tvFinding.text = "Xem kết quả cho: $text"
            }

            views.imgLoading.isVisible = text.toString().length != 0

            if (text.toString().isNotEmpty()){
                myDelayJob?.cancel()
                myDelayJob = CoroutineScope(Dispatchers.Main).launch{
                    delay(500)
                    chatViewModel.handle(ChatViewAction.searchUserByName(text.toString()))
                }
            }
        }

    }

    override fun invalidate(): Unit = withState(chatViewModel){
        when(it.curentUsersSreach){
            is Success ->{
               adapter.setData(it.curentUsersSreach.invoke())

            views.tvExists.isVisible = it.curentUsersSreach.invoke().isEmpty()
            }
            is Fail ->{
                adapter.clearData()
                views.tvExists.isVisible = true
            }
            is Loading ->{
                adapter.clearData()
            }
            else -> {}
        }

        views.imgLoading.isVisible = it.curentUsersSreach is Loading
    }
}