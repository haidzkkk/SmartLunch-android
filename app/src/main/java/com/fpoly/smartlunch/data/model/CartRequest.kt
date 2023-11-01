package com.fpoly.smartlunch.data.model

data class CartRequest (
  val  productId: String,
  val  product_name: String,
  val  product_price: Int,
  var  image: String? ="aa",
  val  purchase_quantity: Int,
  val  sizeId: String
){
}