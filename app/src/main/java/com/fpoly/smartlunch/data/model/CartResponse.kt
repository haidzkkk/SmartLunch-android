package com.fpoly.smartlunch.data.model

import com.fpoly.smartlunch.ultis.StringUltis.dateIso8601Format
import com.fpoly.smartlunch.ultis.convertDateToStringFormat
import java.io.Serializable
import java.util.Date

data class CartResponse(
    var _id: String,
    var userId: User?,
    var couponId: CouponsResponse?,
    var products: ArrayList<ProductCart>,
    var total: Double,
    var totalCoupon: Double?,
    var createdAt: String,
    var updatedAt: String
): Serializable{
}
