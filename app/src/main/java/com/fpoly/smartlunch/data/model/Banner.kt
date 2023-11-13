package com.fpoly.smartlunch.data.model

data class Banner(
    val _id: String,
    val type: Int,
    val img: Image,
    val url: String?
)
// type:
// 0: không có gì
// 1: product
// 2: curpon
// 3: category
//

// url: là đường dẫn tới type đó như sản phảm