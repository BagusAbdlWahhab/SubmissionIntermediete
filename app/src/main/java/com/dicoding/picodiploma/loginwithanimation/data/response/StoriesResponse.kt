package com.dicoding.picodiploma.loginwithanimation.data.response

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryItem>
)

data class StoryItem(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)