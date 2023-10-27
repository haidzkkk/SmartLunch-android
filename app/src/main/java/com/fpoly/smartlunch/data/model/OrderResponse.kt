package com.fpoly.smartlunch.data.model

data class OrderResponse(
    val _id: String,
    val address: String,
    val createdAt: String,
    val notes: String,
    val payerId: String,
    val paymentCode: String,
    val phone: String,
    val products: List<ProductCart>,
    val status: String,
    val total: Int,
    val updatedAt: String,
    val userId: String
)