package com.pt.mysociety.dashboard.expense

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Expense(
    var id: String = "",
    var category: String = "",
    var description: String = "",
    var price: Int = 0,
    var quantity: Int = 1,
    var from: String = "",
    var addedOn: String? = null,
    var settled: Boolean = false,
)
