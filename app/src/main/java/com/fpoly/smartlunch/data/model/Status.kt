package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class Status(
    val __v: Int,
    val _id: String,
    val status_description: String,
    val status_name: String
): Serializable {
}
