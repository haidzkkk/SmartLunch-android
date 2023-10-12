package com.fpoly.smartlunch.ui.main.home

import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.network.RemoteDataSource
import com.fpoly.smartlunch.data.network.UserApi
import com.fpoly.smartlunch.data.repository.TestRepo

class TestViewModelMvRx(state: HomeViewState, private val repo: TestRepo)
    : PolyBaseViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {

    override fun handle(action: HomeViewAction) {
    }

    fun test() = "test"

    companion object : MvRxViewModelFactory<TestViewModelMvRx, HomeViewState> {
        override fun create(viewModelContext: ViewModelContext, state: HomeViewState): TestViewModelMvRx? {
            var repo : TestRepo = TestRepo(RemoteDataSource().buildApi(UserApi::class.java, viewModelContext.activity.applicationContext))
            return TestViewModelMvRx(state, repo)
        }
    }
}