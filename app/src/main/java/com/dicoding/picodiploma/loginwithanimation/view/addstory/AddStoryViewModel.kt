package com.dicoding.picodiploma.loginwithanimation.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.BaseResponse
import java.io.File

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadStory(
        description: String,
        file: File,
        lat: Float? = null,
        lon: Float? = null
    ): LiveData<Result<BaseResponse>> = liveData {
        emit(repository.uploadStory(description, file, lat, lon))
    }
}
