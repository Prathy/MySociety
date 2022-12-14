package com.pt.mysociety.login.model

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: User? = null,
    val error: String? = null
)