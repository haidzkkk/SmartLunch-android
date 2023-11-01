package com.fpoly.smartlunch.data.model

data class CartResponse(
    val _id: String,
    val couponId: String,
    val createdAt: String,
    val products: List<ProductCart>,
    val total: Int,
    val updatedAt: String,
    val userId: String
)