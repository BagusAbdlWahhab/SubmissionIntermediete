package com.dicoding.picodiploma.loginwithanimation.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.response.BaseResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.RegisterRequest
import com.dicoding.picodiploma.loginwithanimation.data.response.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.auth.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.StoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    val apiService: ApiService
) {
    suspend fun getStories() = apiService.getStories()

    suspend fun registerUser(name: String, email: String, password: String): Result<BaseResponse> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            if (!response.error) {
                userPreference.saveUser(UserModel(email, password))
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (!response.error) {
                val token = response.loginResult.token
                ApiConfig.updateToken(token)
                userPreference.saveUser(UserModel(
                    email = email,
                    password = password,
                    token = token,
                    isLogin = true
                ))
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSession(): Flow<UserModel> = userPreference.getSession().onEach { user ->
        // Update token setiap kali session berubah
        ApiConfig.updateToken(user.token.takeIf { user.isLogin })
    }

    suspend fun logout() {
        ApiConfig.updateToken(null)
        userPreference.logout()
    }

    suspend fun getDetailStory(id: String): Result<DetailResponse> {
        return try {
            val response = apiService.getDetailStory(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadStory(
        description: String,
        imageFile: File,
        lat: Float? = null,
        lon: Float? = null
    ): Result<BaseResponse> {
        return try {
            val descriptionPart = description.toRequestBody("text/plain".toMediaType())
            val photoPart = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                .let { MultipartBody.Part.createFormData("photo", imageFile.name, it) }
            val latPart = lat?.toString()?.toRequestBody("text/plain".toMediaType())
            val lonPart = lon?.toString()?.toRequestBody("text/plain".toMediaType())

            val response = apiService.uploadStory(descriptionPart, photoPart, latPart, lonPart)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStoriesWithLocation(): Result<List<StoryItem>> {
        return try {
            val response = apiService.getStoriesWithLocation()
            if (!response.error) {
                Result.success(response.listStory)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getStoryList(): LiveData<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService = ApiConfig.getApiService()
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}