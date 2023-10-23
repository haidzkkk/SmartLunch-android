package com.fpoly.smartlunch.data.model

data class Product_cart(
    val _id: String,
    val image: String,
    val productId: String,
    val product_name: String,
    val product_price: Int,
    val sizeId: String,
)