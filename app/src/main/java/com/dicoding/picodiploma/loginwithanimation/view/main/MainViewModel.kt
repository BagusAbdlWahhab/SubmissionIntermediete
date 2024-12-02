package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.StoryItem
import com.dicoding.picodiploma.loginwithanimation.data.response.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    val story: LiveData<PagingData<StoryItem>> =
        repository.getStoryList().cachedIn(viewModelScope)

    private val _stories = MutableLiveData<List<StoryItem>>()
    val stories: LiveData<List<StoryItem>> = _stories

    private val _storiesWithLocation = MutableLiveData<List<StoryItem>>()
    val storiesWithLocation: LiveData<List<StoryItem>> = _storiesWithLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = repository.getSession().first()
                if (user.isLogin && user.token.isNotEmpty()) {
                    val response = repository.getStories()
                    _stories.value = response.listStory
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "getStories error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            repository.getStoriesWithLocation()
                .onSuccess { stories ->
                    _storiesWithLocation.value = stories
                }
                .onFailure { error ->
                    // Tangani error
                }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}