package com.fpoly.smartlunch.ui.main.love

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentFavouriteBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavouriteFragment : PolyBaseFragment<FragmentFavouriteBinding>() {

    companion object{
        const val TAG = "FavouriteFragment"
    }

    override fun invalidate()  {

    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavouriteBinding = FragmentFavouriteBinding.inflate(layoutInflater)
}