package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class OrderRequest(
    var address: String?,
    var notes: String?,
    var status: String?,
    var statusPayment: String?,
    var isPayment: Boolean?,
    var data: String?,
): Serializable
