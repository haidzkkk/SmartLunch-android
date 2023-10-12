package com.fpoly.smartlunch.core

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.fpoly.smartlunch.ultis.DataSource
import com.fpoly.smartlunch.ultis.PublishDataSource

//import com.fpoly.smartlunch.ultis.DataSource
//import com.fpoly.smartlunch.ultis.PublishDataSource

abstract class PolyBaseViewModel<S: MvRxState, VA: PolyViewAction, VE: PolyViewEvent>(state: S)
    : BaseMvRxViewModel<S>(state, false) {

//    interface Factory<S : MvRxState> {
//        fun create(state: S): BaseMvRxViewModel<S>
//    }

    protected val _viewEvents = PublishDataSource<VE>()
    val viewEvents: DataSource<VE> = _viewEvents

    abstract fun handle(action: VA)
}
