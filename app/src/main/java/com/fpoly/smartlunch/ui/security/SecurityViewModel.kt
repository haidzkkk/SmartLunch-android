package com.fpoly.smartlunch.ui.security

import android.util.Log
import android.widget.Toast
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.fpoly.smartlunch.core.PolyBaseViewModel
import com.fpoly.smartlunch.data.model.Data
import com.fpoly.smartlunch.data.model.ResetPasswordRequest
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.model.UserGGLogin
import com.fpoly.smartlunch.data.model.UserRequest
import com.fpoly.smartlunch.data.model.VerifyOTPRequest
import com.fpoly.smartlunch.data.model.VerifyOTPResponse
import com.fpoly.smartlunch.data.repository.AuthRepository
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException

class SecurityViewModel @AssistedInject constructor(
    @Assisted state: SecurityViewState,
    val repository: AuthRepository,
) :
    PolyBaseViewModel<SecurityViewState,SecurityViewAction,SecurityViewEvent>(state) {

    init {
        handleCurrentUser()
    }

    override fun handle(action: SecurityViewAction) {
        when(action){
            is SecurityViewAction.SignupAction->handleSignup(action.user)
            is SecurityViewAction.LoginGGAction->handleLoginGG(action.user)
            is SecurityViewAction.LoginFBAction->handleLoginFB(action.user)
            is SecurityViewAction.LoginAction->handleLogin(action.userName,action.password)
            is SecurityViewAction.VerifyOTPAction->handleVerifyOTPCode(action.verifyOTPRequest)
            is SecurityViewAction.VerifyOTPResetPassAction->handleVerifyOTPCodeRessetPassword(action.verifyOTPRequest)
            is SecurityViewAction.ResendOTPCode->handleResendOTPCode(action.resendOTPCode)
            is SecurityViewAction.ResendResetPassOTPCode->handleResendResetPasswordOTPCode(action.resendOTPCode)
            is SecurityViewAction.ForgotPassword->handleForgotPassword(action.email)
            is SecurityViewAction.ResetPassword->handleResetPassword(action.resetPasswordRequest)
            else -> {}
        }
    }

    private fun handleLoginGG(user: UserGGLogin) {
        setState {copy(asyncLogin= Loading())}
        repository.loginWithGG(user).execute {
            copy(asyncLogin= it)
        }
    }

    private fun handleLoginFB(user: UserGGLogin) {
        setState {copy(asyncLogin= Loading())}
        repository.loginWithFB(user).execute {
            copy(asyncLogin= it)
        }
    }

    private fun handleResetPassword(resetPasswordRequest: ResetPasswordRequest) {
        setState {copy(asyncUserCurrent= Loading())}
        repository.resetPassword(resetPasswordRequest).execute {
            copy(asyncUserCurrent= it)
        }
    }

    private fun handleForgotPassword(email: String) {
        setState {copy(asyncForgotPassword= Loading())}
        repository.forgotPassword(email).execute {
            copy(asyncForgotPassword= it)
        }
    }

    private fun handleResendResetPasswordOTPCode(resendOTPCode: Data) {
        setState {copy(asyncForgotPassword= Loading())}
        repository.resendOTPCode(resendOTPCode).execute {
            copy(asyncForgotPassword= it)
        }
    }
    private fun handleResendOTPCode(resendOTPCode: Data) {
        setState {copy(asyncSignUp= Loading())}
        repository.resendOTPCode(resendOTPCode).execute {
            copy(asyncSignUp= it)
        }
    }

    private fun handleVerifyOTPCode(verifyOTPRequest: VerifyOTPRequest) {
        setState { copy(asyncUserCurrent= Loading()) }
        repository.verifyOTP(verifyOTPRequest).execute {
            copy(asyncUserCurrent= it)
        }
    }
    private fun handleVerifyOTPCodeRessetPassword(verifyOTPRequest: VerifyOTPRequest) {
        setState { copy(asyncResetPassword= Loading()) }
        repository.verifyOTPChangePassword(verifyOTPRequest).execute {
            copy(asyncResetPassword= it)
        }
    }

    private fun handleCurrentUser() {
        setState { copy(asyncUserCurrent= Loading()) }
        repository.getCurrentUser().execute {
            copy(asyncUserCurrent=it)
        }
    }

    private fun handleLogin(userName:String,password: String){
             setState {
                 copy(asyncLogin = Loading())
             }
        repository.login(userName, password)
            .subscribe(
                { response ->
                    setState {
                        copy(asyncLogin = Success(response))
                    }
                },
                { error ->
                    if (error is HttpException) {
                        val statusCode = error.code()
                        if (statusCode == 500) {
                            val errorBody = error.response()?.errorBody()
                            errorBody?.let {
                                val errorModel = Gson().fromJson(it.string(), VerifyOTPResponse::class.java)
                                setState {
                                    copy(asyncSignUp= Success(errorModel))
                                }
                            }

                        } else {
                            setState {
                                copy(asyncLogin= Fail(error))
                            }
                        }
                    } else {
                    }
                }
            )
    }
    private fun handleSignup(user:UserRequest){
        setState {
            copy(asyncSignUp= Loading())
        }
        repository.signUp(user).execute {
            copy(asyncSignUp= it)
        }
    }

    fun handleRemoveAsyncSignUp(){
        setState {
            copy(asyncSignUp= Uninitialized,asyncUserCurrent= Uninitialized, asyncForgotPassword = Uninitialized)
        }
    }

    fun handleReturnLogin() {
        _viewEvents.post(SecurityViewEvent.ReturnLoginEvent)
    }


    fun handleReturnSignUpEvent() {
        _viewEvents.post(SecurityViewEvent.ReturnSignUpEvent)
    }

    fun handleReturnResetPass() {
        _viewEvents.post(SecurityViewEvent.ReturnResetPassEvent)
    }

    fun handleReturnForgotPass() {
        _viewEvents.post(SecurityViewEvent.ReturnForgotPassEvent)
    }

    fun handleReturnVerifyOTPEvent() {
        _viewEvents.post(SecurityViewEvent.ReturnVerifyOTPEvent)
    }

    fun getString()="test"
    @AssistedFactory
    interface Factory {
        fun create(initialState: SecurityViewState): SecurityViewModel
    }

    companion object : MvRxViewModelFactory<SecurityViewModel, SecurityViewState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: SecurityViewState): SecurityViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}