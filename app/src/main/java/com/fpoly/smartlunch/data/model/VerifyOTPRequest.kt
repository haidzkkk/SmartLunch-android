package com.fpoly.smartlunch.data.model

data class VerifyOTPRequest(
    val userId: String?,
    val otp:String
) {
}