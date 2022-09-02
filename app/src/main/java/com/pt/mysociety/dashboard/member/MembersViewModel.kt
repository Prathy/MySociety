package com.pt.mysociety.dashboard.member

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pt.mysociety.login.model.User

class MembersViewModel constructor(private val database: DatabaseReference = Firebase.database.reference.child("members")) : ViewModel() {

    val members = MutableLiveData<List<User>>()
    val member = MutableLiveData<User>()
    var isLoading = MutableLiveData<Boolean>()
    var isDataExist = MutableLiveData<Boolean>()

    private val childEventListener: ChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            val member: User? = dataSnapshot.getValue(User::class.java)
            if (member != null) {
                member.id = dataSnapshot.key.toString()
                members.value = members.value?.toList()?.plus(member) ?: listOf(member)
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
        Log.d("Dashboard", "init()")
        isLoading.value = true
        members.value = listOf()
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

    fun save(member: User) {
        val key: String = member.id.ifEmpty { database.push().key.toString() }
        member.id = key
        database.child(key).setValue(member)
    }

}