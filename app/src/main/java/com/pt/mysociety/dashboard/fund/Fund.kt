package com.pt.mysociety.dashboard.fund

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Fund(
    var id: String = "",
    var from: String = "",
    var amount: Int = 0,
    var contributedOn: String? = null,
)