package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class Notify(
    val heading: String,
    val title: String,
    val content: String,
    val animationId: Int
) : Serializable