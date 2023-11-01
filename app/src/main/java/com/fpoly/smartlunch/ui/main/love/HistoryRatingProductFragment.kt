package com.fpoly.smartlunch.ui.main.love

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentHistoryRatingProductBinding

class HistoryRatingProductFragment : PolyBaseFragment<FragmentHistoryRatingProductBinding>(){
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoryRatingProductBinding {
        return FragmentHistoryRatingProductBinding.inflate(inflater,container,false)
    }

    override fun invalidate() {
    }

}