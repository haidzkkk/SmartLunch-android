package com.fpoly.smartlunch.ui.main.home

import com.fpoly.smartlunch.core.PolyViewAction
import com.fpoly.smartlunch.ui.chat.ChatViewAction

sealed class HomeViewAction: PolyViewAction {
    object getBanner: HomeViewAction()
    object getDataGallery: HomeViewAction()
}