package com.pt.mysociety.dashboard.sports

import com.google.firebase.database.IgnoreExtraProperties
import com.pt.mysociety.dashboard.expense.Expense
import com.pt.mysociety.dashboard.fund.Fund

@IgnoreExtraProperties
data class Sport(
    var id: String = "",
    var ownerId: String = "",
    var name: String = "",
    var createdOn: String? = null,
    var expenses: List<Expense> = arrayListOf(),
    var funds: List<Fund> = arrayListOf(),
    var equipments: List<Equipment> = arrayListOf(),
    var tag: String = "",
    var expenseCategories: List<String> = arrayListOf(),
    var equipmentCategories: List<String> = arrayListOf()
)

data class Equipment(
    var id: String = "",
    var category: String = "",
    var description: String = "",
    var quantity: Int = 0,
    var status: String = "",
    var updatedOn: String? = null,
)

enum class EquipmentStatus {
    Available,
    UnAvailable
}
