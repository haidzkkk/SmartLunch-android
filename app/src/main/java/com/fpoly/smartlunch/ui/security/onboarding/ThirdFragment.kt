package com.fpoly.smartlunch.ui.security.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentThirdBinding
import com.fpoly.smartlunch.ui.security.LoginFragment
import com.fpoly.smartlunch.ultis.addFragmentToBackstack
import javax.inject.Inject
//done
class ThirdFragment : PolyBaseFragment<FragmentThirdBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        views.next.setOnClickListener {
            (requireActivity() as AppCompatActivity).
            addFragmentToBackstack(R.id.frame_layout, LoginFragment::class.java)
            sessionManager.saveOnBoardingFinished()
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentThirdBinding {
        return FragmentThirdBinding.inflate(inflater,container,false)
    }

    override fun invalidate() {
    }

}