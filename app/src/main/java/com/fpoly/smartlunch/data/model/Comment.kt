package com.fpoly.smartlunch.data.model

data class Comment(
    val _id: String?,
    val userId: User?,
    val productId: String?,
    val orderId: String?,
    val sizeId: String?,
    val sizeName: String?,
    val sizePrice: String?,
    val description: String?,
    val rating: Int,
    val images: ArrayList<Image>?,
    val createdAt: String,
)
data class CommentRequest(
    val productId: String?,
    val sizeId: String?,
    val orderId: String?,
    val description: String?,
    val rating: Int,
)