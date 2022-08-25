package com.pt.mysociety

import android.content.Context
import android.content.SharedPreferences

class SharedPreference constructor(val context: Context) {

    private fun getInstance (): SharedPreferences {
        return context.getSharedPreferences("MY_CREDS_PREFERENCE", Context.MODE_PRIVATE)
    }

    private fun getStringValue (key: String): String {
        return getInstance().getString(key, "").toString()
    }

    private fun setStringValue (key: String, value: String?) {
        val edit = getInstance().edit()
        edit.putString(key, value)
        edit.apply()
    }

    private fun getIntValue (key: String): Int {
        return getInstance().getInt(key, -1)
    }

    private fun setIntValue (key: String, value: Int?) {
        val edit = getInstance().edit()
        edit.putInt(key, value ?: -1)
        edit.apply()
    }

    fun getUserId (): String {
        return getStringValue("KEY_USERID")
    }

    fun setUserId (value: String?) {
        setStringValue("KEY_USERID", value)
    }

    fun getUsername (): String {
        return getStringValue("KEY_USERNAME")
    }

    fun setUsername (value: String?) {
        setStringValue("KEY_USERNAME", value)
    }

    fun getDisplayName (): String {
        return getStringValue("KEY_DISPLAY_NAME")
    }

    fun setDisplayName (value: String?) {
        setStringValue("KEY_DISPLAY_NAME", value)
    }

    fun getWing (): String {
        return getStringValue("KEY_WING")
    }

    fun setWing (value: String?) {
        setStringValue("KEY_WING", value)
    }

    fun getFloor (): Int {
        return getIntValue("KEY_FLOOR")
    }

    fun setFloor (value: Int?) {
        setIntValue("KEY_FLOOR", value)
    }

    fun getHouse (): String {
        return getStringValue("KEY_HOUSE")
    }

    fun setHouse (value: Int?) {
        setIntValue("KEY_HOUSE", value)
    }

    fun getRole (): String {
        return getStringValue("KEY_ROLE")
    }

    fun setRole (value: String?) {
        setStringValue("KEY_ROLE", value)
    }
}