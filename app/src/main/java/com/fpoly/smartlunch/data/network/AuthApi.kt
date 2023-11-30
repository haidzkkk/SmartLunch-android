package com.fpoly.smartlunch.data.network

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
import com.fpoly.smartlunch.ui.security.SecurityViewAction
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {
//    @POST("oauth/token")
//    fun loginWithRefreshToken(@Body credentials: UserCredentials): Call<TokenResponse>
    @POST("api/signin")
    fun login(@Body user: LoginRequest): Observable<TokenResponse>

    @POST("api/signinWithGG")
    fun loginWithGG(@Body user: UserGGLogin): Observable<TokenResponse>
    @POST("api/signinWithFb")
    fun loginWithFb(@Body user: UserGGLogin): Observable<TokenResponse>
    @POST("api/signup")
    fun signUp(@Body user: UserRequest): Observable<VerifyOTPResponse>
    @POST("api/verifyOTP")
    fun verifyOTP(@Body verifyOTP: VerifyOTPRequest):Observable<User>
    @POST("api/verifyOTPChangePassword")
    fun verifyOTPChangePassword(@Body verifyOTP: VerifyOTPRequest):Observable<User>
    @POST("api/resendOTPVerificationCode")
    fun resendOTPCode(@Body resendOTPCode: Data):Observable<VerifyOTPResponse>
    @GET("api/forgotPassword")
    fun forgotPassword(@Query("email") email: String):Observable<VerifyOTPResponse>
    @POST("api/resetPassword")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest):Observable<User>
    @GET("api/getCurrentUser")
    fun getCurrentUser():Observable<User>
    @GET("api/users/{id}")
    fun getUserById(@Path("id") id: String):Observable<User>
    @GET("api/users/search/{text}")
    fun searchUserByName(@Path("text") text: String):Observable<ArrayList<User>>
    @POST("api/update/tokendevice")
    fun updateTokenDevice(@Body tokenDevice: TokenDevice):Observable<User>
}