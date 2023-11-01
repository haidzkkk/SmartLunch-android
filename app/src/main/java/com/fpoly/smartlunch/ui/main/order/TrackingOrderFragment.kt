package com.fpoly.smartlunch.ui.main.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseFragment
import com.fpoly.smartlunch.databinding.FragmentTrackingOrderBinding

class TrackingOrderFragment : PolyBaseFragment<FragmentTrackingOrderBinding>() {


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingOrderBinding {
        return FragmentTrackingOrderBinding.inflate(inflater,container,false)
    }

    override fun invalidate() {
    }

}