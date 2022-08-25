package com.pt.mysociety.dashboard.sports

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Sport(
    var id: String = "",
    var ownerId: String = "",
    var name: String = "",
    var createdOn: String? = null,
    var expenses: List<Expense> = arrayListOf(),
    var funds: List<Fund> = arrayListOf(),
    var equipments: List<Equipment> = arrayListOf(),
    var tag: String = ""
)

data class Expense(
    var id: String = "",
    var category: String = "",
    var description: String = "",
    var price: Int = 0,
    var quantity: Int = 1,
    var buyer: String = "",
    var addedOn: String? = null,
)

data class Fund(
    var id: String = "",
    var from: String = "",
    var amount: Int = 0,
    var contributedOn: String? = null,
)

data class Equipment(
    var id: String = "",
    var category: String = "",
    var description: String = "",
    var quantity: Int = 0,
    var status: String = "",
    var updatedOn: String? = null,
)

enum class ExpenseCategory {
    Bat,
    Ball,
    Grip,
    Breakfast
}

enum class EquipmentCategory {
    Bat,
    Ball,
    Grip
}

enum class EquipmentStatus {
    Available,
    UnAvailable
}