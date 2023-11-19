package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.Notification
import com.fpoly.smartlunch.data.network.NotificationApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.Query

class NotificationRepository(
    private val api: NotificationApi,
) {
    fun getAllNotification(): Observable<List<Notification>> =
        api.getAllNotification().subscribeOn(Schedulers.io())

    fun getAllNotificationWithQuery(isRead: Boolean): Observable<List<Notification>> =
        api.getAllNotificationWithQuery(isRead).subscribeOn(Schedulers.io())

    fun getReadNotification(id: String): Observable<Notification> =
        api.getReadNotification(id).subscribeOn(Schedulers.io())
}