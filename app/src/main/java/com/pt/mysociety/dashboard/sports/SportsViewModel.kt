package com.pt.mysociety.dashboard.sports

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

class SportsViewModel constructor(private val database: DatabaseReference = Firebase.database.reference.child("sports")) : ViewModel() {

    val sports = MutableLiveData<List<Sport>>()
    val sport = MutableLiveData<Sport>()
    val expense = MutableLiveData<Expense>()
    val fund = MutableLiveData<Fund>()
    var isLoading = MutableLiveData<Boolean>()
    var isDataExist = MutableLiveData<Boolean>()

    private val childEventListener: ChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            val sport: Sport? = dataSnapshot.getValue(Sport::class.java)
            if (sport != null) {
                sport.id = dataSnapshot.key.toString()
                sports.value = sports.value?.toList()?.plus(sport) ?: listOf(sport)
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
        sports.value = listOf()
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

    fun save(sport: Sport) {
        val key: String = sport.id.ifEmpty { database.push().key.toString() }
        sport.id = key
        database.child(key).setValue(sport)
    }

    fun getSport(id: String) {
        isLoading.value = true
        database.child(id).get().addOnSuccessListener {
            sport.value = it.getValue(Sport::class.java)
            isLoading.value = false
        }
    }

    fun getSportExpenses(sportId: String, expenseId: String) {
        isLoading.value = true
        database.child(sportId).get().addOnSuccessListener { sport ->
            this.sport.value = sport.getValue(Sport::class.java)
            val expense: Expense? = this.sport.value?.expenses?.find { inventory ->
                inventory.id == expenseId
            }
            if (expense != null) {
                this.expense.value = expense
            }
            isLoading.value = false
        }
    }

    fun getSportFund(sportId: String, fundId: String) {
        isLoading.value = true
        database.child(sportId).get().addOnSuccessListener { sport ->
            this.sport.value = sport.getValue(Sport::class.java)
            val fund: Fund? = this.sport.value?.funds?.find { fund ->
                fund.id == fundId
            }
            if (fund != null) {
                this.fund.value = fund
            }
            isLoading.value = false
        }
    }
}