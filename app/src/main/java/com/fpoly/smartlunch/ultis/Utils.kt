package com.fpoly.smartlunch.ultis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.Fail
import com.fpoly.smartlunch.R
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.ui.chat.ChatActivity
import com.fpoly.smartlunch.ui.security.LoginActivity
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
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
    view.clearFocus()
}

fun Context.showKeyboard(view: View) {
    view.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

@SuppressLint("ShowToast", "ResourceAsColor")
public fun showSnackbar(
    view: View,
    message: String,
    isSuccess: Boolean,
    btnStr: String?,
    onClick: (() -> Unit)?
) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackbar.view.setBackgroundColor(
        ContextCompat.getColor(
            view.context!!,
            if (isSuccess) R.color.green_light else R.color.red_light
        )
    )
    snackbar.setActionTextColor(Color.WHITE)
    snackbar.setAction(btnStr) {
        if (onClick != null) {
            onClick()
        }
    }
    snackbar.show()
}

inline fun androidx.fragment.app.FragmentManager.commitTransaction(allowStateLoss: Boolean = false, func: FragmentTransaction.() -> FragmentTransaction) {
    val transaction = beginTransaction().func()
    if (allowStateLoss) {
        transaction.commitAllowingStateLoss()
    } else {
        transaction.commit()
    }
}

fun AppCompatActivity.popBackStackAndShowPrevious() {
    val fragmentManager = supportFragmentManager
    val backStackEntryCount = fragmentManager.backStackEntryCount

    if (backStackEntryCount > 1) {
        val previousFragmentTag = fragmentManager.getBackStackEntryAt(backStackEntryCount -1).name
        val previousFragment = fragmentManager.findFragmentByTag(previousFragmentTag)
        val transaction = fragmentManager.beginTransaction()
        val fragments = fragmentManager.fragments
        for (f in fragments) {
            if (f != previousFragment) {
                transaction.hide(f)
            }
        }
        transaction.show(previousFragment!!)
        transaction.commit()
    }
    else if(backStackEntryCount == 0){
        finish()
    }
}


fun <T : Fragment> AppCompatActivity.addFragmentToBackStack(
    frameId: Int,
    fragmentClass: Class<T>,
    tag: String? = null,
    option: ((FragmentTransaction) -> Unit)? = null,
    bundle: Bundle? = null
) {
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    val newFragment = fragmentManager.findFragmentByTag(tag)
    if (newFragment == null) {
        val instance = fragmentClass.newInstance().apply {
            bundle?.let { arguments = it }
        }
        transaction.add(frameId, instance, tag).addToBackStack(tag)
    } else {
        transaction.show(newFragment)
    }

    val fragments = fragmentManager.fragments
    Log.d("TAG1", "addFragmentToBackStack: "+fragments.size)
    for (f in fragments) {
        if (f != newFragment) {
            transaction.hide(f)
        }
    }
    option?.invoke(transaction)
    transaction.commit()
}

fun <T : Fragment> Fragment.addFragmentToBackStack(
    frameId: Int,
    fragmentClass: Class<T>,
    tag: String? = null,
    option: ((FragmentTransaction) -> Unit)? = null,
    bundle: Bundle? = null
) {
    val fragmentManager = childFragmentManager
    val transaction = fragmentManager.beginTransaction()
    val newFragment = fragmentManager.findFragmentByTag(tag)
    if (newFragment == null) {
        val instance = fragmentClass.newInstance().apply {
            bundle?.let { arguments = it }
        }
        transaction.add(frameId, instance, tag).addToBackStack(tag)
    } else {
        transaction.show(newFragment)
    }

    val fragments = fragmentManager.fragments
    Log.d("TAG1", "addFragmentToBackStack: "+fragments.size)
    for (f in fragments) {
        if (f != newFragment) {
            transaction.hide(f)
        }
    }
    option?.invoke(transaction)
    transaction.commit()
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

fun getRealPathFromURI(context: Context, uri: Uri?): String? {
    var cursor: Cursor? = null
    try {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(uri!!, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return cursor.getString(columnIndex)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor!!.close()
    }
    return null
}

fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
}

fun Int.toBitMap(resources: Resources) = BitmapFactory.decodeResource(resources, this)

fun <T> checkStatusApiRes(err: Fail<T>): Int {
    return when (err.error.message!!.trim()) {
        "HTTP 200" -> {
            R.string.http200
        }

        "HTTP 400" -> {
            R.string.http400
        }

        "HTTP 401" -> {
            R.string.http401
        }

        "HTTP 403" -> {
            R.string.http403
        }

        "HTTP 404" -> {
            R.string.http404
        }

        "HTTP 500" -> {
            R.string.http500
        }

        else -> {
            R.string.http500
        }
    }
}

fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val marginLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        marginLayoutParams.setMargins(left, top, right, bottom)
        this.requestLayout()
    }
}

fun TextView.setTextColor(isSuccess: Boolean){
    if (isSuccess){
        this.setTextColor(this.context.getColor(R.color.green))
    }else{
        this.setTextColor(this.context.getColor(R.color.red))
    }
}

fun Activity.startActivityWithData(intent: Intent, type: String?, idUrl: String?){
    intent.apply {
        putExtras(Bundle().apply {
            putString("type", type)
            putString("idUrl", idUrl) }
        )
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}

fun Context.uriToFilePath(uri: Uri?): String {
    return when (uri?.scheme) {
        "file" -> {
            uri.path ?: ""
        }
        "content" -> {
            getFilePathFromContentUri(this, uri)
        }

        else -> ""
    }
}

private fun getFilePathFromContentUri(context: Context, uri: Uri): String {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)

    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            if (columnIndex != -1) {
                return it.getString(columnIndex) ?: ""
            }
        }
    }
    return ""
}