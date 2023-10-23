package com.fpoly.smartlunch.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentChangePasswordBinding
import com.fpoly.smartlunch.ui.security.SecurityViewModel

class ChangePasswordFragment : PolyBaseFragment<FragmentChangePasswordBinding>() {
   // private val viewModel: SecurityViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenEvent()
    }

    private fun listenEvent() {

    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChangePasswordBinding {
        return FragmentChangePasswordBinding.inflate(inflater,container,false);
    }

    override fun invalidate() {

    }
//
//    override fun invalidate():Unit = withState(viewModel){
//
//    }

}