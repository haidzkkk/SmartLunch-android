package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class CartRequest (
  val  productId: String,
  val  product_name: String,
  val  product_price: Double,
  var  image: String? ="aa",
  val  purchase_quantity: Int,
  val  sizeId: String,
  val  toppings: List<Topping>?
): Serializable {
}