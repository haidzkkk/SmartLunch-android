package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.OrderZaloPayReponse
import com.fpoly.smartlunch.data.model.OrderZaloPayRequest
import com.fpoly.smartlunch.data.model.RefundOrderZaloPayReponse
import com.fpoly.smartlunch.data.model.StatusOrderZaloPayReponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ZalopayApi {
    @POST("/v2/create")
    fun postOrder(@Header("Content-Type") contentType: String, @Body requestBody: RequestBody): Observable<OrderZaloPayReponse>
    @POST("/v2/query")
    fun getOrder(@Header("Content-Type") contentType: String, @Body requestBody: RequestBody): Observable<StatusOrderZaloPayReponse>
    @POST("/v2/refund")
    fun refundOrder(@Header("Content-Type") contentType: String, @Body requestBody: RequestBody): Observable<RefundOrderZaloPayReponse>
}