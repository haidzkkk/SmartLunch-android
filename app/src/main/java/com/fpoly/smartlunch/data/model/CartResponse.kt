package com.fpoly.smartlunch.data.model

data class CartResponse(
    val _id: String,
    val userId: User,
    val couponId: CouponsResponse?,
    val products: ArrayList<ProductCart>,
    val total: Double,
    val totalCoupon: Double?,
    val createdAt: String,
    val updatedAt: String
)