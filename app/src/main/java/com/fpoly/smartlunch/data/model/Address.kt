package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class Address(
    val _id: String,
    val recipientName: String,
    val phoneNumber: String,
    val addressLine: String,
    val latitude: Double,
    val longitude: Double,
    val userId: String,
    var isSelected: Boolean
):Serializable
