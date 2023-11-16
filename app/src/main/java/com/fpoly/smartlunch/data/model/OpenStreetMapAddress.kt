package com.fpoly.smartlunch.data.model

data class OpenStreetMapAddress(
    val `ISO3166-2-lvl4`: String,
    val city: String,
    val country: String,
    val country_code: String,
    val postcode: String,
    val quarter: String,
    val road: String,
    val suburb: String
)