package com.fpoly.smartlunch.data.model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class Address(
    val __v: Int,
    val _id: String,
    val addressLine: String,
    val isRemove: Boolean,
    val distance: Double,
    val deliveryFee: Int,
    val deliveryTime: Double,
    var isSelected: Boolean,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String,
    val recipientName: String,
    val userId: User
): Serializable {
    fun toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
}

