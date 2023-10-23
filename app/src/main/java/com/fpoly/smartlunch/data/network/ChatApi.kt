package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {

    @GET("/api/room")
    fun getRoom(): Observable<ArrayList<Room>>

    @GET("/api/message/{id}")
    fun getMessage(@Path("id") idRoom: String): Observable<ArrayList<Message>>

    @POST("/api/message")
    fun postMessage(@Body message: Message): Observable<Message>

}