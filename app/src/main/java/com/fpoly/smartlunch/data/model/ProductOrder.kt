package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class ProductOrder(
    val _id: String,
    val productId: String,
    val product_name: String,
    val image: String,
    val purchase_quantity: Int,
    val sizeId: String,
    val sizeName: String,
    val product_price: Double,
    val product_discount: Double,
    val total: Double,
): Serializable