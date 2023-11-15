package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class Image(
    var publicId: String? = null,
    var url: String? = null
) : Serializable {
    constructor() : this(null, null)
}