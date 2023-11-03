package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class OrderResponse(
    val _id: String,
    val address: Address,
    val createdAt: String,
    val couponId: String,
    val notes: String,
    val payerId: String,
    val paymentCode: String,
    val products: List<ProductCart>,
    val discount: Int,
    val status: Status,
    val total: Int,
    val updatedAt: String,
    val userId: String
):Serializable