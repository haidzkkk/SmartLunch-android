package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class OrderRequest(
    var address: String? = null,
    var notes: String? = null,
    var payerId: String? = null,
    var paymentCode: String? = null,
    var paymentId: String? = null,
    var products: List<ProductCart>,
    var total: Int,
    var discount: Int,
    var couponId: String? = null,
    var userId: String
) : Serializable