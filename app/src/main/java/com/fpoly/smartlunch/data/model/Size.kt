package com.fpoly.smartlunch.data.model

import java.io.Serializable

data class Size (
    val _id: String,
    val size_name: String,
    val size_price: Double,
    val productId: String
):Serializable{
    private var _isSelect: Boolean = false
    var isSelect: Boolean
        set(value) {
            this._isSelect = value
        }
        get() = _isSelect
}