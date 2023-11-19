package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.Notification
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {

    @GET("/api/notification")
    fun getAllNotificationWithQuery(@Query("isRead") isRead: Boolean): Observable<List<Notification>>

    @GET("/api/notification")
    fun getAllNotification(): Observable<List<Notification>>

    @GET("/api/notification/read/{id}")
    fun getReadNotification(@Path("id") id: String): Observable<Notification>

}