package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.Comment
import com.fpoly.smartlunch.data.model.Message
import com.fpoly.smartlunch.data.model.Room
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApi {
    @GET("/api/comment/{productId}")
    fun getCommentProduct(@Path("productId") productId: String): Observable<ArrayList<Comment>>
    @GET("/api/comment/{productId}")
    fun getCommentProductLimit(@Path("productId") productId: String, @Query("limitPosition") limitPosition: Int): Observable<ArrayList<Comment>>

    @Multipart
    @POST("/api/comment")
    fun postComment(
        @Part("productId") productId: RequestBody,
        @Part("orderId") orderId: RequestBody,
        @Part("sizeId") sizeId: RequestBody,
        @Part("description") description: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Observable<Comment>
}