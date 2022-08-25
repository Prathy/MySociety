package com.pt.mysociety.data

import android.content.Context
import com.pt.mysociety.SharedPreference
import com.pt.mysociety.login.model.User

object UserHelper {

    const val roleUser = "user"
    private const val roleOrganizer = "organizer"
    private const val roleAdmin = "admin"

    fun isAdmin(context: Context): Boolean {
        return SharedPreference(context).getRole() == roleAdmin
    }

    fun isOrganizer(context: Context): Boolean {
        return SharedPreference(context).getRole() == roleOrganizer
    }

    fun updateUserPref(context: Context, user: User?){
        if(user != null){
            val sharedPreference = SharedPreference(context)
            sharedPreference.setUserId(user.userId)
            sharedPreference.setUsername(user.email)
            sharedPreference.setRole(user.role)
            sharedPreference.setDisplayName(user.displayName)
            sharedPreference.setWing(user.wing)
            sharedPreference.setFloor(user.floor)
            sharedPreference.setHouse(user.house)
        }
    }
}