package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class OrderResponse(
    val _id: String,
    val userId: User,
    val couponId: String,
    val products: List<ProductOrder>,
    val deliveryFee: Double,
    val discount: Double,
    val total: Double,
    val totalAll: Double,
    val status: Status,
    val shipperId: String,
    val address: Address,
    val notes: String,
    val statusPayment: Status,
    val isPayment: Boolean,
    val data: String?,
    val createdAt: String,
    val updatedAt: String,
):Serializable