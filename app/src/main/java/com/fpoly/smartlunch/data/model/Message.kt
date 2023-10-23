package com.fpoly.smartlunch.data.model


object MessageType{
    const val TYPE_TEXT = 0
    const val TYPE_IMAGE = 1
    const val TYPE_PRODUCT = 2
    const val TYPE_REPLY = 3
    const val TYPE_CALL = 4
}

data class Message(
    val _id: String?,
    val roomId: String?,
    val userIdSend: User?,
    val message: String?,
    val linkMessage: String?,
    val images: Array<Image>?,
    val type: Int?,
    val time: String?,
)