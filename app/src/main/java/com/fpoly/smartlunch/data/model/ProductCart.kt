package com.fpoly.smartlunch.data.model

import java.io.Serializable
data class ProductCart(
    val _id: String,
    val productId: Product,
    val purchase_quantity : Int,
    val sizeId: Size,
    val toppings: ArrayList<ToppingCart>
):Serializable

data class ToppingCart(
    val _id: Topping,
    val _quantity : Int
):Serializable
