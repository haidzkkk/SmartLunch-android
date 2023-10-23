package com.fpoly.smartlunch.data.model

data class Room(
    val _id: String?,
    val shopUserId: User?,
    val userUserId: User?,
    val userIdSend: User?,
    val messSent: String?,
    val timeSent: String?,
    val seen: Boolean?,
) {
}