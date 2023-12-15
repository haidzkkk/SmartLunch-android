package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.District
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.data.model.OrderZaloPayReponse
import com.fpoly.smartlunch.data.model.OrderZaloPayRequest
import com.fpoly.smartlunch.data.model.Province
import com.fpoly.smartlunch.data.model.ProvinceAddress
import com.fpoly.smartlunch.data.model.RefundOrderZaloPayReponse
import com.fpoly.smartlunch.data.model.StatusOrderZaloPayReponse
import com.fpoly.smartlunch.data.model.Ward
import com.fpoly.smartlunch.data.network.OrderApi
import com.fpoly.smartlunch.data.network.ProvinceAddressApi
import com.fpoly.smartlunch.data.network.ZalopayApi
import com.fpoly.smartlunch.ultis.getTypePayments
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    val api: OrderApi,
    val zalopayApi: ZalopayApi
) {

    fun getTypePayment(): Observable<ArrayList<Menu>> = getTypePayments()
    fun createOrderZaloPay(requestBody: RequestBody): Observable<OrderZaloPayReponse>
    = zalopayApi.postOrder("application/x-www-form-urlencoded", requestBody).subscribeOn(Schedulers.io())
    fun getStatusOrderZaloPay(requestBody: RequestBody): Observable<StatusOrderZaloPayReponse>
    = zalopayApi.getOrder("application/x-www-form-urlencoded", requestBody).subscribeOn(Schedulers.io())
    fun createRefundOrderZaloPay(requestBody: RequestBody): Observable<RefundOrderZaloPayReponse>
    = zalopayApi.refundOrder("application/x-www-form-urlencoded", requestBody).subscribeOn(Schedulers.io())
}