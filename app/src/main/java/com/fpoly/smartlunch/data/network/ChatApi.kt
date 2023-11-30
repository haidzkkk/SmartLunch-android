package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.Room
import com.fpoly.smartlunch.data.model.User
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ChatApi {

    @GET("/api/room/auth")
    fun getRoom(): Observable<ArrayList<Room>>
    @GET("/api/room/{id}")
    fun getRoomById(@Path("id") roomId: String): Observable<Room>
    @GET("/api/room/auth/{id}")
    fun getRoomWithUserId(@Path("id") wituUserId: String): Observable<Room>

    @GET("/api/message/{id}")
    fun getMessage(@Path("id") roomId: String): Observable<ArrayList<Message>>

    @GET("/api/message/call/{id}")
    fun getLastCallMessage(@Path("id") roomId: String): Observable<Message>

    @Multipart
    @POST("/api/message")
    fun postMessage(
        @Part("roomId") roomId: RequestBody,
        @Part("message") message: RequestBody,
        @Part("linkMessage") linkMessage: RequestBody,
        @Part("type") type: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Observable<Message>

    @POST("/api/message/call")
    fun postMessageCall(@Body message: Message): Observable<Message>

}