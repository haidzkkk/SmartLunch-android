package com.fpoly.smartlunch.ui.main.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentCardBinding
import com.fpoly.smartlunch.databinding.FragmentOrderBinding

class OrderFragment : PolyBaseFragment<FragmentOrderBinding>() {
    companion object{
        const val TAG = "OrderFragment"
    }

    override fun invalidate() {
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentOrderBinding
    = FragmentOrderBinding.inflate(layoutInflater)
}