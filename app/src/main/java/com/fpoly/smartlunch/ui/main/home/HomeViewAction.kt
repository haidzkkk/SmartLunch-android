package com.fpoly.smartlunch.ui.main.home

import com.fpoly.smartlunch.core.PolyViewAction

sealed class HomeViewAction: PolyViewAction {
    object TestViewAction: HomeViewAction()
    object GetUserViewAction: HomeViewAction()
}