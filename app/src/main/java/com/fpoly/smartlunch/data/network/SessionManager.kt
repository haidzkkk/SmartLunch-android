package com.fpoly.smartlunch.data.network

import android.content.Context
import android.content.SharedPreferences
import com.fpoly.smartlunch.R

class SessionManager(context: Context) {
    companion object {
        const val USER_TOKEN = "user_token"
        const val TOKEN_ACCESS="access_token"
        const val TOKEN_REFRESH="refresh_token"
        const val DARK_MODE = "dark_mode"
        const val LANGUAGE = "language"
    }

    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    //access token
    fun saveAuthTokenAccess(token: String) {
        val editor = prefs.edit()
        editor.putString(TOKEN_ACCESS, token)
        editor.apply()
    }

    fun fetchAuthTokenAccess(): String? {
        return prefs.getString(TOKEN_ACCESS, null)
    }

    fun removeTokenAccess() {
        val editor = prefs.edit()
        editor.remove(TOKEN_ACCESS).apply()
    }

    //refresh token
    fun saveAuthTokenRefresh(token: String) {
        val editor = prefs.edit()
        editor.putString(TOKEN_REFRESH, token)
        editor.apply()
    }

    fun fetchAuthTokenRefresh(): String? {
        return prefs.getString(TOKEN_REFRESH, null)
    }

    fun removeTokenRefresh() {
        val editor = prefs.edit()
        editor.remove(TOKEN_REFRESH).apply()
    }

    //theme
    fun saveDarkMode(isDarkMode: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(DARK_MODE, isDarkMode)
        editor.apply()
    }

    fun fetchDarkMode(): Boolean {
        return prefs.getBoolean(DARK_MODE, false)
    }

    //language
    fun saveLanguage(language: String) {
        val editor = prefs.edit()
        editor.putString(LANGUAGE, language)
        editor.apply()
    }
    fun fetchLanguage(): String {
        return prefs.getString(LANGUAGE, "vi").toString()
    }

    //onboarding
    fun saveOnBoardingFinished(){
        val editor=prefs.edit()
        editor.putBoolean("Finished",true)
        editor.apply()
    }
    fun getOnBoardingFinished(): Boolean {
        return prefs.getBoolean("Finished", false)
    }
}