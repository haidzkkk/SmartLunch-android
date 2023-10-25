package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.ChangePassword
import com.fpoly.smartlunch.data.model.TokenResponse
import com.fpoly.smartlunch.data.model.UpdateUserRequest
import com.fpoly.smartlunch.data.model.User
import com.fpoly.smartlunch.data.network.UserApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

class UserRepository(
    private val api: UserApi
) {
    fun changePassword(changePassword: ChangePassword): Observable<TokenResponse> =
        api.changePassword(changePassword).subscribeOn(Schedulers.io())

    fun getCurrentUser(): Observable<User> = api.getCurrentUser().subscribeOn(Schedulers.io())

    fun getUserById(id: String): Observable<User> = api.getUserById(id).subscribeOn(Schedulers.io())

    fun updateUser(updateUserRequest: UpdateUserRequest): Observable<User> =
        api.updateUser(updateUserRequest).subscribeOn(Schedulers.io())

    fun uploadAvatar(avatar: MultipartBody.Part): Observable<User> =
        api.uploadAvatar(avatar).subscribeOn(Schedulers.io())
}