package com.fpoly.smartlunch.data.model

data class Address(
    val _id: String,
    val recipientName: String,
    val phoneNumber: String,
    val addressLine: String,
    val latitude: Double,
    val longitude: Double,
    val userId: String,
    var isSelected: Boolean
)
