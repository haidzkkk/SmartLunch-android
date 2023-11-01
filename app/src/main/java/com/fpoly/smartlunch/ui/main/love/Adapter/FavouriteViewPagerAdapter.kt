package com.fpoly.smartlunch.ui.main.love.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fpoly.smartlunch.ui.main.love.FavouriteFragment
import com.fpoly.smartlunch.ui.main.love.FavouriteProductsFragment
import com.fpoly.smartlunch.ui.main.love.HistoryRatingProductFragment
import com.fpoly.smartlunch.ui.main.order.CurrentOrdersFragment
import com.fpoly.smartlunch.ui.main.order.HistoryOrderFragment

class FavouriteViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return FavouriteProductsFragment()
            1 -> return HistoryRatingProductFragment()
        }
        return CurrentOrdersFragment();
    }

}