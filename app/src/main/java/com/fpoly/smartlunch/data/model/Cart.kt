package com.fpoly.smartlunch.data.model

data class Cart(
    val _id: String,
    val couponId: Any,
    val createdAt: String,
    val products: List<Product_cart>,
    val total: Int,
    val updatedAt: String,
    val userId: String
)