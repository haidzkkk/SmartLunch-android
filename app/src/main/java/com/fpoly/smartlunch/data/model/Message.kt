package com.fpoly.smartlunch.data.model


object MessageType{
    const val TYPE_TEXT = 0
    const val TYPE_IMAGE = 1
    const val TYPE_PRODUCT = 2
    const val TYPE_REPLY = 3
    const val TYPE_CALLING = 11
    const val TYPE_CALLED = 12
}

data class Message(
    var _id: String?,
    var roomId: String?,
    var userIdSend: User?,
    var message: String?,
    var linkMessage: String?,
    val images: Array<Image>?,
    var type: Int?,
    val sdp: ArrayList<String>,
    val iceCandidate: ArrayList<String>,
    var createdAt: String?,
    var updatedAt: String?,
)