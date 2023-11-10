package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.District
import com.fpoly.smartlunch.data.model.Menu
import com.fpoly.smartlunch.data.model.Province
import com.fpoly.smartlunch.data.model.ProvinceAddress
import com.fpoly.smartlunch.data.model.Ward
import com.fpoly.smartlunch.data.network.OrderApi
import com.fpoly.smartlunch.data.network.ProvinceAddressApi
import com.fpoly.smartlunch.ultis.getTypePayments
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    val api: OrderApi,
    val apiProvince: ProvinceAddressApi,
) {

    fun getTypePayment(): Observable<ArrayList<Menu>> = getTypePayments()

    fun getProvince(): Observable<ProvinceAddress<Province>> = apiProvince.getProvince().subscribeOn(Schedulers.io())
    fun getDistrict(idProvince: String): Observable<ProvinceAddress<District>> = apiProvince.getDistrict(idProvince).subscribeOn(Schedulers.io())
    fun getWard(idDistrict: String): Observable<ProvinceAddress<Ward>> = apiProvince.getWard(idDistrict).subscribeOn(Schedulers.io())

}