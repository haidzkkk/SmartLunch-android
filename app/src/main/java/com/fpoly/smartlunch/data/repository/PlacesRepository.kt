package com.fpoly.smartlunch.data.repository

import com.fpoly.smartlunch.data.model.OpenStreetMapResponse
import com.fpoly.smartlunch.data.network.PlacesApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PlacesRepository @Inject constructor(
    private val api: PlacesApi
) {
    fun getLocationName(
        latitude: Double,
        longitude: Double
    ): Observable<OpenStreetMapResponse> =
        api.getLocationName(latitude, longitude).subscribeOn(Schedulers.io())
}