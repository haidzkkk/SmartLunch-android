package com.fpoly.smartlunch.ui.security.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentSecondBinding
//done
class SecondFragment : PolyBaseFragment<FragmentSecondBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenEvent()
    }

    private fun listenEvent() {
        val viewPager2=activity?.findViewById<ViewPager2>(R.id.view_pager)
        views.apply {
            textView3.setOnClickListener {
                viewPager2?.currentItem=2
            }
            next.setOnClickListener {
                viewPager2?.currentItem=2
            }
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSecondBinding {
        return FragmentSecondBinding.inflate(inflater,container,false)
    }

    override fun invalidate() {
    }

}