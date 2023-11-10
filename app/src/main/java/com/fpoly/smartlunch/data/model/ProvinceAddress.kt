package com.fpoly.smartlunch.data.model

data class ProvinceAddress<T>(
    var results: ArrayList<T>
)

data class Province(
    var province_id: String,
    var province_name: String,
    var province_type: String,
){
    override fun toString(): String {
        return province_name
    }
}

data class District(
    var district_id: String,
    var district_name: String,
    var district_type: String,
    var province_id: String,
){
    override fun toString(): String {
        return district_name
    }
}

data class Ward(
    var ward_id: String,
    var ward_name: String,
    var ward_type: String,
    var district_id: String,
){
    override fun toString(): String {
        return ward_name
    }
}