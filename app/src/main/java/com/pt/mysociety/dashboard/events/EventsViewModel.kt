package com.pt.mysociety.dashboard.events

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pt.mysociety.dashboard.expense.Expense
import com.pt.mysociety.dashboard.fund.Fund

class EventsViewModel constructor(private val database: DatabaseReference = Firebase.database.reference.child("events")) : ViewModel() {

    val events = MutableLiveData<List<Event>>()
    val event = MutableLiveData<Event>()
    val expense = MutableLiveData<Expense>()
    val fund = MutableLiveData<Fund>()
    var isLoading = MutableLiveData<Boolean>()
    var isDataExist = MutableLiveData<Boolean>()
    var isDeleted = MutableLiveData<Boolean>()

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

    fun getEvent(id: String) {
        isLoading.value = true
        database.child(id).get().addOnSuccessListener {
            event.value = it.getValue(Event::class.java)
            isLoading.value = false
        }
    }

    fun getEventExpenses(eventId: String, expenseId: String) {
        isLoading.value = true
        database.child(eventId).get().addOnSuccessListener { event ->
            this.event.value = event.getValue(Event::class.java)
            val expense: Expense? = this.event.value?.expenses?.get(expenseId)
            if (expense != null) {
                this.expense.value = expense
            }
            isLoading.value = false
        }
    }

    fun getEventFund(eventId: String, fundId: String) {
        isLoading.value = true
        database.child(eventId).get().addOnSuccessListener { event ->
            this.event.value = event.getValue(Event::class.java)
            val fund: Fund? = this.event.value?.funds?.get(fundId)
            if (fund != null) {
                this.fund.value = fund
            }
            isLoading.value = false
        }
    }

    fun deleteFund(eventId: String, fundId: String) {
        isDeleted.value = false
        database.child(eventId).get().addOnSuccessListener { _event ->
            val event = _event.getValue(Event::class.java)
            if(event != null){
                event.funds.remove(fundId)
                database.child(eventId).setValue(event).addOnCompleteListener{
                    isDeleted.value = true
                }
            }
        }
    }
}