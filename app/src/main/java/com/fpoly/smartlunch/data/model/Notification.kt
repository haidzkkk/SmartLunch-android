package com.fpoly.smartlunch.data.model

data class Notification(
    val _id: String,
    val userId: String,
    val title: String,
    val content: String,
    val type: String,
    val idUrl: String,
    val timestamp: String,
    val isRead: Boolean
) {
}