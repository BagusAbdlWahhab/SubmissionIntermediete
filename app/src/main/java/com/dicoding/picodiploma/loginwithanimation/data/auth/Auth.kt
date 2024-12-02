package com.dicoding.picodiploma.loginwithanimation.data.auth

import okhttp3.Interceptor
import okhttp3.Response

class Auth : Interceptor {
    private var token: String? = null

    fun setToken(newToken: String?) {
        token = newToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return token?.let { nonNullToken ->
            val modifiedRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $nonNullToken")
                .build()
            chain.proceed(modifiedRequest)
        } ?: chain.proceed(originalRequest)
    }
}