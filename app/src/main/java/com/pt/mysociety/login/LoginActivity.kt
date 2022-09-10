package com.pt.mysociety.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pt.mysociety.BaseActivity
import com.pt.mysociety.InAppUpdate
import com.pt.mysociety.dashboard.DashboardActivity
import com.pt.mysociety.helpers.UserHelper
import com.pt.mysociety.databinding.ActivityLoginBinding
import com.pt.mysociety.login.model.User
import com.pt.mysociety.login.view.LoginViewModel
import com.pt.mysociety.login.view.LoginViewModelFactory
import com.pt.mysociety.profile.ProfileActivity
import java.util.*
import java.util.concurrent.TimeUnit


class LoginActivity : BaseActivity() {

    private lateinit var inAppUpdate: InAppUpdate
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inAppUpdate = InAppUpdate(this)

        val diffInDays: Long = TimeUnit.MILLISECONDS.toDays(Date().time - sharedPreference.getLastLoginTime())
        if(diffInDays < 1){
            startActivity(Intent(this, DashboardActivity::class.java))
            setResult(Activity.RESULT_OK)
            finish()
        }

        val username = binding.username
        username.setText(sharedPreference.getUsername())
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]
        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        if(loginViewModel.loginFormState.value?.isDataValid == true) {
                            loginViewModel.login(
                                this@LoginActivity,
                                username.text.toString(),
                                password.text.toString()
                            )
                        }
                }
                false
            }

            login.setOnClickListener {
                if(loginViewModel.loginFormState.value?.isDataValid == true) {
                    loading.visibility = View.VISIBLE
                    loginViewModel.login(
                        this@LoginActivity,
                        username.text.toString(),
                        password.text.toString()
                    )
                }
            }
        }
    }

    private fun updateUiWithUser(user: User) {
        UserHelper.updateUserPref(this, user)
        Firebase.database.reference.child("users").child(user.id).get().addOnCompleteListener { task ->
            val loggedInUser: User? = if(task.result.value != null) task.result.getValue(User::class.java) else null
            loggedInUser?.id = user.id
            UserHelper.updateUserPref(this, loggedInUser)
            val intent = if(loggedInUser != null && loggedInUser.updated) {
                Intent(this, DashboardActivity::class.java)
            } else {
                Intent(this, ProfileActivity::class.java)
            }

            sharedPreference.setLastLoginTime(Date().time)
            startActivity(intent)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        inAppUpdate.onActivityResult(requestCode,resultCode, data)
    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        inAppUpdate.onDestroy()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}