package com.fpoly.smartlunch.data.model

data class Category(
    val _id: String,
    val category_image: Image,
    val category_name: String,
    val createdAt: String,
    val deleted: Boolean,
    val products: List<String>,
    val updatedAt: String
)