package com.fpoly.smartlunch.ui.security.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentViewPagerBinding

//done
class ViewPagerFragment : PolyBaseFragment<FragmentViewPagerBinding>() {

    lateinit var viewPager2: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        viewPager2 = views.viewPager
        val fragmentArrayList = arrayListOf<Fragment>(
            FirstFragment(),
            SecondFragment(),
            ThirdFragment()
        )
        val adapter = ViewPagerAdapter(
            fragmentArrayList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        viewPager2.adapter = adapter
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentViewPagerBinding {
        return FragmentViewPagerBinding.inflate(inflater, container, false)
    }

    override fun invalidate() {
    }

}