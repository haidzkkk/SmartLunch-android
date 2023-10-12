package com.fpoly.smartlunch.ultis

import android.content.res.Resources
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import com.fpoly.smartlunch.R
import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.checkNull(res : Resources): Boolean{
    if (this.text.toString().trim() == ""){
        this.error = res.getString(R.string.notEmpty)
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

fun checkValidEmail(res : Resources , edt : TextInputEditText): Boolean {
    val strEmail = edt.text.toString().trim()
    if (TextUtils.isEmpty(strEmail) or !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
        edt.error =   res.getString(R.string.validateEmail)

        return true
    }
    edt.error = null
    return false
}

fun checkValidEPassword(res : Resources , edt1 : TextInputEditText, edt2 : TextInputEditText): Boolean {
    val str1 = edt1.text.toString().trim()
    val str2 = edt2.text.toString().trim()
    if (str1 == ""|| str2 == "" || str1 != str2){
        edt2.error =   res.getString(R.string.validatePassword)
        return true
    }
    edt2.error = null
    return false
}