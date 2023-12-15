package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.OrderZaloPayReponse
import com.fpoly.smartlunch.data.model.OrderZaloPayRequest
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ZalopayApi {
    @POST("/v2/create")
    fun postOrder(@Header("Content-Type") contentType: String,
                  @Body requestBody: RequestBody)
    : Observable<OrderZaloPayReponse>
}