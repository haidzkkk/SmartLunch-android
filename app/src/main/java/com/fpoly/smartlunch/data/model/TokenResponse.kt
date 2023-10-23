package com.fpoly.smartlunch.data.model

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    val accessToken: String?,
    val refreshToken: String?
)