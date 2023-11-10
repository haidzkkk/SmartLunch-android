package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.District
import com.fpoly.smartlunch.data.model.Province
import com.fpoly.smartlunch.data.model.ProvinceAddress
import com.fpoly.smartlunch.data.model.Ward
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ProvinceAddressApi {
    @GET("api/province")
    fun getProvince(): Observable<ProvinceAddress<Province>>
    @GET("api/province/district/{province_id}")
    fun getDistrict(@Path("province_id") idProvince: String): Observable<ProvinceAddress<District>>
    @GET("api/province/ward/{district_id}")
    fun getWard(@Path("district_id") idDistrict: String): Observable<ProvinceAddress<Ward>>
}