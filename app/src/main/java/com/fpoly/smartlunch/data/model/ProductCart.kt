package com.fpoly.smartlunch.data.model

import java.io.Serializable
data class ProductCart(
    val _id: String,
    val image: String,
    val productId: String,
    val product_name: String,
    val product_price: Int,
    val purchase_quantity : Int,
    val sizeId: String,
):Serializable