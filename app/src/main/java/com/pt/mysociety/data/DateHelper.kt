package com.pt.mysociety.data

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    @SuppressLint("SimpleDateFormat")
    fun toSimpleString(date: Date? = Date()) : String {
        val format = SimpleDateFormat("dd/MM/yyy")
        return format.format(date!!)
    }
}