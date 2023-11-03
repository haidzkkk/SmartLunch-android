package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class Status(
    val _id: String,
    val status_name: String,
    val status_description: String,
): Serializable