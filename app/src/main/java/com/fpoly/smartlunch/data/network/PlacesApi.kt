package com.fpoly.smartlunch.data.network

import com.fpoly.smartlunch.data.model.OpenStreetMapResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {
    @GET("reverse?format=json")
    fun getLocationName(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Observable<OpenStreetMapResponse>
}