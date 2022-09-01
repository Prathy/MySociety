package com.pt.mysociety.login.model

data class User(
    var id: String = "",
    var name: String? = "",
    var wing: String? = "",
    var floor: Int? = -1,
    var house: Int? = -1,
    var email: String? = "",
    var contact: String? = "",
    var role: String? = "user",
    var updated: Boolean = false
)