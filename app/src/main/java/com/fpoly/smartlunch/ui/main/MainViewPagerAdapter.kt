package com.fpoly.smartlunch.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fpoly.smartlunch.ui.main.cart.OrderFragment
import com.fpoly.smartlunch.ui.main.home.HomeFragment
import com.fpoly.smartlunch.ui.main.love.FavouriteFragment
import com.fpoly.smartlunch.ui.main.profile.ProfileFragment

class MainViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return HomeFragment()
            1 -> return FavouriteFragment()
            2 -> return OrderFragment()
            3 -> return OrderFragment()
            4 -> return ProfileFragment()
        }
        return HomeFragment();
    }

}