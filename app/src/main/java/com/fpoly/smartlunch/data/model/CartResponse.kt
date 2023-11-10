package com.fpoly.smartlunch.data.model

data class CartResponse(
    val _id: String,
    val couponId: CouponsResponse?,
    val createdAt: String,
    val products: ArrayList<ProductCart>,
    val total: Double,
    val totalCoupon: Double?,
    val updatedAt: String,
    val userId: User
)