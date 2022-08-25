package com.pt.mysociety.dashboard.events

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EventsViewModel constructor(private val database: DatabaseReference = Firebase.database.reference.child("events")) : ViewModel() {

    val events = MutableLiveData<List<Event>>()
    var isLoading = MutableLiveData<Boolean>()
    var isDataExist = MutableLiveData<Boolean>()

    private val childEventListener: ChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            val event: Event? = dataSnapshot.getValue(Event::class.java)
            event?.id = dataSnapshot.key.toString()
            if (event != null) {
                events.value = events.value?.toList()?.plus(event) ?: listOf(event)
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    fun init() {
        isLoading.value = true
        events.value = listOf()
        database.addChildEventListener(childEventListener)
        database.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let {
                    isDataExist.value = it.hasChildren()
                }
            }
            isLoading.value = false
        }
    }

    fun onPause() {
        database.removeEventListener(childEventListener)
    }

    fun save(event: Event) {
        val key: String = event.id.ifEmpty { database.push().key.toString() }
        event.id = key
        database.child(key).setValue(event)
    }
}