package com.fpoly.smartlunch.ui.chat.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fpoly.smartlunch.core.PolyBaseBottomSheet
import com.fpoly.smartlunch.databinding.BottomSheetGalleryChatBinding

class GalleryChatBottomSheetFragment : PolyBaseBottomSheet<BottomSheetGalleryChatBinding>() {
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): BottomSheetGalleryChatBinding = BottomSheetGalleryChatBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun invalidate() {
    }
}