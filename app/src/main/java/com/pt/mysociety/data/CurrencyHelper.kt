package com.pt.mysociety.data

import java.text.NumberFormat
import java.util.*

object CurrencyHelper {

    fun convertToRupees(amount: Int): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("INR")

        return format.format(amount)
    }
}