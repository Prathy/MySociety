package com.pt.mysociety.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pt.mysociety.login.model.User

class ProfileViewModel constructor(private val database: DatabaseReference = Firebase.database.reference.child("users")) : ViewModel() {

    private lateinit var loggedInUserId: String
    val profile = MutableLiveData<User>()
    var isLoading = MutableLiveData<Boolean>()
    var isProfileUpdated = MutableLiveData<Boolean>()

    private val childEventListener: ChildEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            if(loggedInUserId == dataSnapshot.key.toString()) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                user?.userId = dataSnapshot.key.toString()
                profile.value = user!!
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

    fun init(userId: String) {
        loggedInUserId = userId
        isLoading.value = true
        database.addChildEventListener(childEventListener)
    }

    fun save(user: User) {
        database.child(user.userId).setValue(user).addOnCompleteListener {
            isProfileUpdated.value = it.isSuccessful
        }
    }
}