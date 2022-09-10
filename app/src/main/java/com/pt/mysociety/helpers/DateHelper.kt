package com.pt.mysociety.helpers

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object DateHelper {

    fun toSimpleString(date: Date? = Date()) : String {
        val format = SimpleDateFormat("dd/MM/yyy")
        return format.format(date!!)
    }

    fun toDate(dateStr: String?): Date? {
        val dateTimeFormatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateStr?.let { dateTimeFormatter.parse(it) }
    }

    fun toNormalize(dateStr: String?): String? {
        val dateTimeFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy")
        return dateStr?.let { toDate(it)?.let { it1 -> dateTimeFormatter.format(it1) } }
    }
}