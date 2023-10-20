package com.funetuneapps.bloombundy.classes

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object SharedPrefUtils {
    const val firstTime = "firstTime"

    private lateinit var sharedPreferences: SharedPreferences
    fun init(app: Application) {
        sharedPreferences = app.getSharedPreferences("OUTFIT_PREFS", Context.MODE_PRIVATE)
    }

    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun setBool(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBool(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue).toString()
    }
}