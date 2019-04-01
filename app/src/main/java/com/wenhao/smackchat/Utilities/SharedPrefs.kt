package com.wenhao.smackchat.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

class SharedPrefs(context: Context) {

    val PREFS_FILENAME = "prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val IS_LOGGED_IN = "isLoggedIn"
    val AUTH_TOKEN = "authToken"
    val USER_EMAIL = "userEmail"

    val requestQueue = Volley.newRequestQueue(context)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(this.IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(this.IS_LOGGED_IN, value).apply()

    var authToken: String
        get() = prefs.getString(this.AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(this.AUTH_TOKEN, value).apply()

    var userEmail: String
        get() = prefs.getString(this.USER_EMAIL, "")
        set(value) = prefs.edit().putString(this.USER_EMAIL, value).apply()
}