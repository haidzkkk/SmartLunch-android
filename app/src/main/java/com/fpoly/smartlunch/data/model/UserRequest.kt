package com.fpoly.smartlunch.data.model

data class UserRequest(
    var first_name:String ="",
    var last_name:String ="",
    var email:String ="",
    var password:String ="",
    var confirmPassword: String ="",
    var phone: String ="",
) {

}