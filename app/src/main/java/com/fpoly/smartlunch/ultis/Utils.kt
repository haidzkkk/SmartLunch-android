package com.fpoly.smartlunch.ultis

import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatDelegate
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