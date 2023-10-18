package com.fpoly.smartlunch.ui.main.home

import com.fpoly.smartlunch.core.PolyViewAction

sealed class HomeViewAction: PolyViewAction {

//    object GetUserViewAction: HomeViewAction()
    object GetListProduct : HomeViewAction()
}