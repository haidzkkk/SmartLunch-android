package com.fpoly.smartlunch.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.airbnb.mvrx.viewModel
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseActivity
import com.fpoly.smartlunch.core.example.ChatViewState
import com.fpoly.smartlunch.data.network.SocketManager
import com.fpoly.smartlunch.databinding.ActivityChatBinding
import javax.inject.Inject

class ChatActivity : PolyBaseActivity<ActivityChatBinding>(), ChatViewmodel.Factory {

    private lateinit var navController: NavController
    private val chatViewmodel: ChatViewmodel by viewModel()

    @Inject
    lateinit var chatViewmodelFactory: ChatViewmodel.Factory

    override fun getBinding(): ActivityChatBinding = ActivityChatBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PolyApplication).polyConponent.inject(this)
        super.onCreate(savedInstanceState)

        initUI()
        handleViewMolde()
    }

    private fun handleViewMolde() {
        chatViewmodel.handle(ChatViewAction.getCurentUser)
        chatViewmodel.observeViewEvents {
            when(it){
                is ChatViewEvent.ReturnSetupAppbar -> handleAppbar(it.isVisible, it.isTextView, it.tvTitle, it.isVisibleIconCall)
                else -> {}
            }
        }
    }
    private fun initUI() {
        setSupportActionBar(views.toolBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        navController = findNavController(R.id.nav_fragment)
        handleAppbar(true, true,"Đoạn chat", false)
    }

    private fun handleAppbar(isVisible: Boolean, isTextView: Boolean, tvTitle: String, isVisibleIconCall: Boolean){
        views.appBar.isVisible = isVisible

        views.tvTitle.isVisible = isTextView
        views.tilTitle.isVisible = !isTextView
        if (isTextView) views.tvTitle.text = tvTitle
        else views.edtTitle.setText(tvTitle)

        views.imgCall.isVisible = isVisibleIconCall
        views.imgCallVideo.isVisible = isVisibleIconCall
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun create(initialState: ChatViewState): ChatViewmodel {
        return chatViewmodelFactory.create(initialState)
    }

}
