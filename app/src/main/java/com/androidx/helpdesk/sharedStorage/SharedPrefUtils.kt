package com.androidx.helpdesk.sharedStorage

import android.content.Context

object SharedPrefUtils {

    fun getString(context: Context?, tag: String?): String? {
        val settings = context!!.getSharedPreferences(Constants.USER_PREFERENCE, 0)
        return settings.getString(tag, null)
    }

    fun putString(context: Context, tag: String?, value: String?) {
        val settings = context.getSharedPreferences(Constants.USER_PREFERENCE, 0)
        val editor = settings.edit()
        editor.putString(tag, value)
        editor.apply()
    }

    fun getBoolean(context: Context, tag: String?): Boolean {
        val settings = context.getSharedPreferences(Constants.USER_PREFERENCE, 0)
        return settings.getBoolean(tag, false)
    }

    fun putBoolean(context: Context, tag: String?, value: Boolean) {
        val settings = context.getSharedPreferences(Constants.USER_PREFERENCE, 0)
        val editor = settings.edit()
        editor.putBoolean(tag, value)
        editor.apply()
    }

    fun getInt(context: Context?, tag: String?): Int {
        val settings = context!!.getSharedPreferences(Constants.USER_PREFERENCE, 0)
        return settings.getInt(tag, 0)
    }

    fun putInt(context: Context, tag: String?, value: Int) {
        val settings = context.getSharedPreferences(Constants.USER_PREFERENCE, 0)
        val editor = settings.edit()
        editor.putInt(tag, value)
        editor.apply()
    }
}