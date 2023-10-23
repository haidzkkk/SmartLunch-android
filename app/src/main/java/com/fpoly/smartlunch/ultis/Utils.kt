package com.fpoly.smartlunch.ultis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.fpoly.smartlunch.R
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

fun changeMode(isChecked: Boolean) {
    if (isChecked) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
fun changeLangue(resources: Resources, lang: String) {
    val res: Resources = resources
    val dm: DisplayMetrics = res.displayMetrics
    val conf: Configuration = res.configuration
    res.updateConfiguration(conf, dm)
    conf.setLocale(Locale(lang))
}

fun Activity.startActivityAnim(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
}

fun Activity.popActivityAnim() {
    finish()
    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

@SuppressLint("ShowToast", "ResourceAsColor")
public fun showSnackbar(view: View, message: String, isSuccess: Boolean, btnStr: String?, onClick: () -> Unit){
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context!!, if (isSuccess) R.color.green_light else R.color.red_light))
    snackbar.setActionTextColor(Color.WHITE)
    snackbar.setAction(btnStr){ onClick() }
    snackbar.show()
}