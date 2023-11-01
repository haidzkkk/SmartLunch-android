package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class OrderRequest(
    var address: String,
    var consignee_name: String,
    var notes: String,
    var payerId: String,
    var paymentCode: String,
    var paymentId: String,
    var phone: String,
    var products: List<ProductCart>,
    var total: Int,
    var discount: Int,
    var userId: String
): Serializable