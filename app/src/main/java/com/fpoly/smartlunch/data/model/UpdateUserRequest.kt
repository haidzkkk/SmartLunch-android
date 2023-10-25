package com.fpoly.smartlunch.data.model

data class UpdateUserRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String,
    val birthday: String,
    val gender: Boolean
)
