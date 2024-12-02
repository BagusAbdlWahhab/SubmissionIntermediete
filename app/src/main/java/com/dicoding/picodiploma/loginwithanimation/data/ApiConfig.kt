package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.auth.Auth
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        @Volatile
        private var instance: ApiService? = null
        private val auth = Auth()

        fun updateToken(token: String?) {
            auth.setToken(token)
        }

        fun getApiService(): ApiService {
            return instance ?: synchronized(this) {
                val loggingInterceptor = HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
                val client = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(auth)
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://story-api.dicoding.dev/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                retrofit.create(ApiService::class.java).also {
                    instance = it
                }
            }
        }
    }
}