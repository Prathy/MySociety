package com.pt.mysociety.dashboard.events

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event(
    var id: String = "",
    var ownerId: String = "",
    var name: String = ""

) {
    @Exclude
    fun toMap(): Map<String, String> {
        val result = HashMap<String, String>()
        result["id"] = id
        result[name] = name
        return result
    }

    @Exclude
    fun fromMap(map: Map<String, String>) = object {
        val id by map
        val name by map

        val eventObj = Event(id, name)
    }.eventObj
}