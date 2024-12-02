package com.dicoding.picodiploma.loginwithanimation.data.response

data class UserModel(
    val email: String,
    val password: String,
    val token: String = "",
    val isLogin: Boolean = false
)

data class BaseResponse(
    val error: Boolean,
    val message: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)

// Request Models
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)