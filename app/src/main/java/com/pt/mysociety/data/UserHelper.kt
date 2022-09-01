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
            sharedPreference.setUserId(user.id)
            sharedPreference.setUsername(user.email)
            sharedPreference.setRole(user.role)
            sharedPreference.setDisplayName(user.name)
            sharedPreference.setWing(user.wing)
            sharedPreference.setFloor(user.floor)
            sharedPreference.setHouse(user.house)
        }
    }

    fun getHouse(member: User): String {
        if(member.id.isEmpty()){
            return ""
        }

        val wing = member.wing
        val floor = if(member.floor!! < 10) "0" + member.floor else member.floor
        val house = if(member.house!! < 10) "0" + member.house else member.house
        return "$wing-$floor$house"
    }
}