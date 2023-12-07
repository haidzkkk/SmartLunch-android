package com.fpoly.smartlunch.data.model

data class ChangeQuantityRequest(
   val  purchase_quantity : Int,
    val sizeId : String,
    val toppingId: String?

)

