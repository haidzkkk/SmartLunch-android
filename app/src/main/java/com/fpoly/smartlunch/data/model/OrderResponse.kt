package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class OrderResponse(
    val _id: String,
    val userId: User,
    val couponId: String,
    val products: List<ProductCart>,
    val discount: Int,
    val total: Int,
    val status: Status,

    val address: Address,
    val consignee_name: String,
    val notes: String,
    var statusPayment: Status,
    var isPayment: Boolean,
    val createdAt: String,
    val updatedAt: String,
):Serializable