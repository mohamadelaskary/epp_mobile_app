package net.gbs.epp_project.Base

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences




class LocalStorage(context: Context) {

    private val PREF_NAME = "local_storage_prefs"
    private val FIRST_TIME_KEY = "firstTimeKey"
    private val STORED_DEVICE_DATE_KEY = "StoredDeviceDate"
    private val STORED_ACTUAL_DATE_KEY = "StoredActualDate"
    private val STORED_ACTUAL_FULL_DATE_TIME_KEY = "StoredActualFullDateTime"

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getStoredDeviceDate(): String? {
        return sharedPreferences.getString(STORED_DEVICE_DATE_KEY, "")
    }

    fun getStoredActualDate(): String? {
        return sharedPreferences.getString(STORED_ACTUAL_DATE_KEY, "")
    }

    fun getStoredActualFullDateTime(): String? {
        return sharedPreferences.getString(STORED_ACTUAL_FULL_DATE_TIME_KEY, "")
    }

    fun setStoredDeviceDate(date: String) {
        editor.putString(STORED_DEVICE_DATE_KEY, date).apply()
    }

    fun setStoredActualDate(date: String) {
        editor.putString(STORED_ACTUAL_DATE_KEY, date).apply()
    }

    fun setStoredActualFullDateTime(date: String) {
        editor.putString(STORED_ACTUAL_FULL_DATE_TIME_KEY, date).apply()
    }

    fun setFirstTime(firstTime: Boolean) {
        editor.putBoolean(FIRST_TIME_KEY, firstTime).apply()
    }

    fun getFirstTime(): Boolean {
        return sharedPreferences.getBoolean(FIRST_TIME_KEY, true)
    }
}
