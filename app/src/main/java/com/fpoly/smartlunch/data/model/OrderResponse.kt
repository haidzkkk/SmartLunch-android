package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class OrderResponse(
    val _id: String,
    val address: Address,
    val couponId: String,
    val createdAt: String,
    val discount: Int,
    val isPayment: Boolean,
    val notes: String,
    val products: List<ProductCart>,
    val shipperId: String,
    val status: Status,
    val statusPayment: Status,
    val total: Int,
    val updatedAt: String,
    val userId: User
):Serializable