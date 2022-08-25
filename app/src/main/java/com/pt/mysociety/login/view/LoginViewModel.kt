package com.pt.mysociety.login.view

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.pt.mysociety.R
import com.pt.mysociety.login.data.LoginFormState
import com.pt.mysociety.login.model.User
import com.pt.mysociety.login.model.LoginResult

class LoginViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(activity: Activity, username: String, password: String) {

        Firebase.auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    _loginResult.value =
                        LoginResult(success = task.result.user?.let { User(it.uid, it.email) })
                } else {
                    _loginResult.value = LoginResult(error = task.exception?.message.toString())
                }
            }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}