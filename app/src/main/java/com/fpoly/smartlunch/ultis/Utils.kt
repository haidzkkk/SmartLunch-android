package com.fpoly.smartlunch.ultis

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.ui.security.LoginActivity
import java.util.Locale

fun changeMode(isChecked: Boolean) {
    if (isChecked) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
fun Activity.changeLanguage(lang: String) {
    val res: Resources = resources
    val dm: DisplayMetrics = res.displayMetrics
    val conf: Configuration = res.configuration
    val myLocale = Locale(lang)
    conf.setLocale(myLocale)
    res.updateConfiguration(conf, dm)
}

inline fun androidx.fragment.app.FragmentManager.commitTransaction(allowStateLoss: Boolean = false, func: FragmentTransaction.() -> FragmentTransaction) {
    val transaction = beginTransaction().func()
    if (allowStateLoss) {
        transaction.commitAllowingStateLoss()
    } else {
        transaction.commit()
    }
}
fun <T : Fragment> AppCompatActivity.addFragmentToBackstack(
    frameId: Int,
    fragmentClass: Class<T>,
    tag: String? = null,
    allowStateLoss: Boolean = true,
    option: ((FragmentTransaction) -> Unit)? = null,
    bundle: Bundle?=null
) {
    supportFragmentManager.
    commitTransaction(allowStateLoss) {
        option?.invoke(this)
        replace(frameId, fragmentClass,bundle, tag).addToBackStack(tag)
    }
}

fun Activity.handleLogOut() {
    SessionManager(applicationContext).also {
        it.removeTokenAccess()
    }

    val intent = Intent(this, LoginActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
    finish()
}