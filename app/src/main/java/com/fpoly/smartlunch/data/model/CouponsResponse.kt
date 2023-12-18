package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class CouponsResponse(
    val _id: String,
    val coupon_name: String,
    val coupon_code: String,
    val coupon_content: String,
    val coupon_images: ArrayList<Image>,
    val coupon_quantity: Int,
    val discount_amount: Int,
    val expiration_date: String,
    val min_purchase_amount: Int
): Serializable
