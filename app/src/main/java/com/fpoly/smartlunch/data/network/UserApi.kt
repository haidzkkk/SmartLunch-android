package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.Image
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.UpdateUserRequest
import com.fpoly.smartlunch.data.model.User
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApi {
    @GET("api/getCurrentUser")
    fun getCurrentUser():Observable<User>
    @GET("api/users/{id}")
    fun getUserById(@Path("id") id: String):Observable<User>
    @POST("api/changePassword")
    fun changePassword(@Body changePassword: ChangePassword):Observable<TokenResponse>
    @PATCH("api/users")
    fun updateUser(@Body updateUserRequest: UpdateUserRequest):Observable<User>
    @Multipart
    @PATCH("api/users/uploadAvatar")
    fun uploadAvatar(@Part avatar: MultipartBody.Part):Observable<User>
}