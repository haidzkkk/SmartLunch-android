package com.fpoly.smartlunch.data.model

data class UserGGLogin(
    val googleId: String?=null,
    val first_name: String?=null,
    val last_name: String?=null,
    val email: String?=null,
    val password: String?=null,
) {
}