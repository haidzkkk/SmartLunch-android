package com.fpoly.smartlunch.ui.call.waiting

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.model.RequireCall
import com.fpoly.smartlunch.data.model.RequireCallType
import com.fpoly.smartlunch.databinding.FragmentWaitingBinding
import com.fpoly.smartlunch.ui.call.CallViewModel
import com.fpoly.smartlunch.ui.call.call.CallChatFragment
import com.fpoly.smartlunch.ultis.commitTransaction
import org.webrtc.SessionDescription

@SuppressLint("SetTextI18n")
class WaitingFragment: PolyBaseFragment<FragmentWaitingBinding>(){

    val callViewModel: CallViewModel by activityViewModel()
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWaitingBinding {
        return FragmentWaitingBinding.inflate(layoutInflater)
    }

    override fun invalidate() : Unit = withState(callViewModel) {
        when(it.curentRoom){
            is Success ->{
                val curentRoom = it.curentRoom.invoke()

                views.tvNameLoading.text = "${curentRoom.shopUserId?.last_name} ${curentRoom.shopUserId?.first_name}"
                views.tvMessage.text = "${curentRoom.shopUserId?.first_name} muốn gọi điện với bạn"

                views.btnAccept.setOnClickListener { v ->
                    requireActivity().supportFragmentManager.commitTransaction {
                        replace(R.id.frame_layout, CallChatFragment())
                    }
                }

                views.btnRefuse.setOnClickListener { v ->
                    callViewModel.sendDataMessageCallToServerSocket(RequireCall(RequireCallType.CREATE_ANSWER, null, null, RequireCallType.NO))
                    requireActivity().finish()
                }
            }
            else ->{

            }
        }
        when(it.requireCall?.type){
            RequireCallType.STOP_RECEIVED ->{
                Toast.makeText(requireContext(), "Cuộc gọi đã kết thúc", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

}