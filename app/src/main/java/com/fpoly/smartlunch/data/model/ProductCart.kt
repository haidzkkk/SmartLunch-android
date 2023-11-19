package com.fpoly.smartlunch.data.model

import java.io.Serializable
data class ProductCart(
    val _id: String,
    val productId: Product,
    val purchase_quantity : Int,
    val sizeId: Size,
):Serializable
