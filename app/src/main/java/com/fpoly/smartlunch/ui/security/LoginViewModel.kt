package com.fpoly.smartlunch.ui.security

import android.util.Log
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.repository.AuthRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LoginViewModel @AssistedInject constructor(
    @Assisted state: LoginViewState,
    private val repository: AuthRepository
): PolyBaseViewModel<LoginViewState, LoginViewAction, LoginViewEvent>(state){

    init {
        handleGetTokenFcm()
    }

    override fun handle(action: LoginViewAction) {
        when(action){
            is LoginViewAction.LoginAction -> handleLogin(action.userName, action.password)
            is LoginViewAction.GetUserAction -> handleGetUser()
        }
    }

    private fun handleLogin(userName: String, password: String) {
        setState {
            copy(asyncLogin=Loading())
        }
        // c1: extention của mvrx
        repository.login(userName,password).execute {
            Thread.sleep(1000)
            copy(asyncLogin=it)
        }
        // c2: đăng ký rx như bth
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { tokenRespon ->
//                        setState { copy(asyncLogin = Success(tokenRespon)) }
//                    },
//                    { err ->
//                        setState { copy(asyncLogin = Fail(err)) }
//                    })
    }

    private fun handleGetUser() {
        
    }

    private fun handleGetTokenFcm(){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                setState {
                    copy(tokenFcm = it.result)
                }
                Log.e("handleGetTokenFcm", "token FCM: " + it.result);
            }
    }

    public fun handleFragmentSignin() {
        _viewEvents.post(LoginViewEvent.ToFragmentViewEvent(R.id.signinFragment))
    }

    public fun handleBackToFragment() {
        _viewEvents.post(LoginViewEvent.ReturnFragmentViewEvent)
    }


    @AssistedFactory
    interface Factory {
        fun create(initialState: LoginViewState): LoginViewModel
    }

    companion object : MvRxViewModelFactory<LoginViewModel, LoginViewState> {
        override fun create(viewModelContext: ViewModelContext, state: LoginViewState): LoginViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}