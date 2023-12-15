package com.fpoly.smartlunch.ui.security.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.fpoly.smartlunch.PolyApplication
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.databinding.FragmentFirstBinding
import com.fpoly.smartlunch.ui.security.LoginFragment
import com.fpoly.smartlunch.ultis.addFragmentToBackstack
import javax.inject.Inject

//done
class FirstFragment : PolyBaseFragment<FragmentFirstBinding>() {
    @Inject
    lateinit var sessionManager: SessionManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as PolyApplication).polyComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        listenEvent()
    }

    private fun listenEvent() {
        val viewPager2=activity?.findViewById<ViewPager2>(R.id.view_pager)
        views.apply {
            textView3.setOnClickListener {
                (requireActivity() as AppCompatActivity).
                addFragmentToBackstack(R.id.frame_layout, LoginFragment::class.java)
                sessionManager.saveOnBoardingFinished()
            }
            next.setOnClickListener {
                viewPager2?.currentItem=1
            }
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFirstBinding {
        return FragmentFirstBinding.inflate(inflater,container,false)
    }

    override fun invalidate() {
    }

}