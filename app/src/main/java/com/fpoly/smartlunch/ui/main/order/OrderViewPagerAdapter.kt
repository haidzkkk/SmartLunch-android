package com.fpoly.smartlunch.ui.main.order

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class OrderViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return WaitingConfirmOrderFragment()
            1 -> return ConfirmedFragment()
            2 -> return CurrentOrdersFragment()
            3 -> return HistoryOrderFragment()
            4 -> return CancelledOrdersFragment()
        }
        return WaitingConfirmOrderFragment();
    }

}