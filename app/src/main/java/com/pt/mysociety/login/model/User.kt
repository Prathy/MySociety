package com.pt.mysociety.login.model

data class User(
    var userId: String = "",
    var email: String? = "",
    var displayName: String? = "",
    var wing: String? = "",
    var floor: Int? = -1,
    var house: Int? = -1,
    var role: String? = "user",
    var updated: Boolean = false
)