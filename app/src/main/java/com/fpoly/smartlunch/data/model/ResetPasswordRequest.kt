package com.fpoly.smartlunch.data.model

data class ResetPasswordRequest(
    val userId: String,
    val newPassword: String,
    val confirmPassword: String
)