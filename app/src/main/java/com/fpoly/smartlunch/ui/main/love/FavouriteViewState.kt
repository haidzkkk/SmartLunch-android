package com.fpoly.smartlunch.ui.main.love

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.fpoly.smartlunch.data.model.Favourite
import com.fpoly.smartlunch.data.model.Size

data class FavouriteViewState  (

    var Favourite: Async<List<Favourite>> = Uninitialized,

    ): MvRxState{
}