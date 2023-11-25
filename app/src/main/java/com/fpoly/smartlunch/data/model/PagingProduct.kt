package com.fpoly.smartlunch.data.model

data class PagingRequestProduct(
    val limit: Int?,        // lấy bao nhiêu
    val sort: String?,      // lấy theo tên product, ...
    val order: String?,     // desc và asc
    val page: Int?,         // page là kiểu muốn lấy lượt thứ mấy ấy
    val query: String?,      // q: query tìm kiếm theo tên sp iphone
) {
}

object SortPagingProduct{
    const val name: String = "product_name"
    const val price: String = "product_price"
    const val quantity: String = "sold_quantity"
    const val views: String = "views"
    const val bought: String = "bought"
    const val rate: String = "rate_count"
    const val isActive: String = "isActive"
}