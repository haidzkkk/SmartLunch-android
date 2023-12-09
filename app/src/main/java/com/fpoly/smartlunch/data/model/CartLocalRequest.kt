package com.fpoly.smartlunch.data.model

data class CartLocalRequest<T>(
    val cart: CartResponse,
    val data: T?
) {
}