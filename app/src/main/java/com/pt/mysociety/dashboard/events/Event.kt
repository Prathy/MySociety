package com.pt.mysociety.dashboard.events

import com.google.firebase.database.IgnoreExtraProperties
import com.pt.mysociety.dashboard.expense.Expense
import com.pt.mysociety.dashboard.fund.Fund

@IgnoreExtraProperties
data class Event(
    var id: String = "",
    var ownerId: String = "",
    var name: String = "",
    var tag: String = "",
    var expenses: HashMap<String, Expense> = HashMap(),
    var funds: HashMap<String, Fund> = HashMap(),
    var createdOn: String = "",
    var minContribution: Int = 0,
    var eventDate: String = "",
    var expenseCategories: List<String> = arrayListOf(),
)

