package com.fpoly.smartlunch.ui.main.card

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentCardBinding

class CardFragment : PolyBaseFragment<FragmentCardBinding>() {
    companion object{
        const val TAG = "CardFragment"
    }
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCardBinding {
        return FragmentCardBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.layoutHeader.tvTitleToolbar. text = "Thẻ"
    }

    override fun invalidate() {
    }

}