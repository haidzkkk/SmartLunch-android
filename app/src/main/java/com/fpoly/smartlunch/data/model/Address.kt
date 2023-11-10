package com.fpoly.smartlunch.data.model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class Address(
    val _id: String?,
    val recipientName: String,
    val phoneNumber: String,
    val addressLine: String,
    val latitude: Double,
    val longitude: Double,
    val userId: User?,
    var isSelected: Boolean
): Serializable {
    fun toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
}

