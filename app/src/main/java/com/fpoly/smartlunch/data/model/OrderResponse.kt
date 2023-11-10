package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class OrderResponse(
    val _id: String,
    val userId: User,
    val products: List<ProductCart>,
    val discount: Int,
    val total: Int,
    val notes: String,
    val phone: String,
    val createdAt: String,
    val updatedAt: String,
    val address: Address,
    val consignee_name: String,
    val status: Status,
    var statusPayment: Status,
    var isPayment: Boolean
):Serializable