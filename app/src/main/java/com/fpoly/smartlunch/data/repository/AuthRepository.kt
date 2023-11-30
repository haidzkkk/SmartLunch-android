package com.fpoly.smartlunch.data.repository

import android.util.Log
import com.fpoly.smartlunch.data.model.Data
import com.fpoly.smartlunch.data.model.LoginRequest
import com.fpoly.smartlunch.data.model.ResetPasswordRequest
import com.fpoly.smartlunch.data.model.TokenDevice
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.model.UserGGLogin
import com.fpoly.smartlunch.data.model.UserRequest
import com.fpoly.smartlunch.data.model.VerifyOTPRequest
import com.fpoly.smartlunch.data.model.VerifyOTPResponse
import com.fpoly.smartlunch.data.network.AuthApi
import com.fpoly.smartlunch.ui.security.SecurityViewAction
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.Body

class AuthRepository(
    private val api: AuthApi,
) {
    fun signUp(user : UserRequest):Observable<VerifyOTPResponse> = api.signUp(
        user
    ).subscribeOn(Schedulers.io())
    fun login(email: String, password: String): Observable<TokenResponse> = api.login(
        LoginRequest(email,password)
    ).subscribeOn(Schedulers.io())

    fun loginWithGG(user: UserGGLogin): Observable<TokenResponse> = api.loginWithGG(user).subscribeOn(Schedulers.io())
    fun loginWithFB(user: UserGGLogin): Observable<TokenResponse> = api.loginWithFb(user).subscribeOn(Schedulers.io())

    fun verifyOTP(verifyOTP: VerifyOTPRequest):Observable<User> = api.verifyOTP(verifyOTP).subscribeOn(Schedulers.io())
    fun verifyOTPChangePassword(verifyOTP: VerifyOTPRequest):Observable<User> = api.verifyOTPChangePassword(verifyOTP).subscribeOn(Schedulers.io())
    fun resendOTPCode(resendOTPCode: Data):Observable<VerifyOTPResponse> = api.resendOTPCode(resendOTPCode).subscribeOn(Schedulers.io())

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest):Observable<User> =api.resetPassword(resetPasswordRequest).subscribeOn(Schedulers.io())

    fun forgotPassword(email: String):Observable<VerifyOTPResponse> = api.forgotPassword(email).subscribeOn(Schedulers.io())

    fun getCurrentUser():Observable<User> = api.getCurrentUser().subscribeOn(Schedulers.io())
}