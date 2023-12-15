package com.fpoly.smartlunch.ultis

import android.content.res.Resources
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import com.fpoly.smartlunch.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.checkNull(res : Resources): Boolean{
    if (this.text.toString().trim() == ""){
        this.error = res?.getString(R.string.notEmpty)
        return true
    }
    this.error = null
    return false
}

fun EditText.checkNull(res : Resources): Boolean{
    if (this.text.toString().trim() == ""){
        this.error = res.getString(R.string.notEmpty)
        return true
    }
    this.error = null
    return false
}

fun TextInputLayout.checkValidEmail(res : Resources?): Boolean {
    val strEmail = this.editText?.text.toString().trim()
    if (TextUtils.isEmpty(strEmail) or !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
        this.error =   res?.getString(R.string.validateEmail)
        return true
    }
    this.error = null
    return false
}

fun TextInputLayout.checkValidEPassword(res : Resources , edt1 : TextInputEditText, edt2 : TextInputEditText): Boolean {
    val str1 = edt1.text.toString().trim()
    val str2 = edt2.text.toString().trim()
    if (str1 == ""|| str2 == "" || str1 != str2){
        edt2.error =   res.getString(R.string.validatePassword)
        return true
    }
    edt2.error = null
    return false
}

fun EditText.checkPhoneNumberValid(res : Resources?): Boolean {
    val regex = """(0[1-9][0-9]{8,9})""".toRegex()
    var isCheck = regex.matches(this.text.toString().trim())

    if (!isCheck){
        this.error = res?.getString(R.string.validatePhone)
        return true
    }
    this.error = null
    return false
}