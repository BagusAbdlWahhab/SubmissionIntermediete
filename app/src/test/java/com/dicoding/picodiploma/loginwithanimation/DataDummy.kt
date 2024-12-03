package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.data.response.StoryItem
import kotlin.text.Typography.quote

object DataDummy {

    fun generateDummyQuoteResponse(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = StoryItem(
                "photoUri $i",
                "TimeZone $1",
                "User $i",
                "Description $i",
                i.toString(),
                1.1,
                1.1
            )
            items.add(story)
        }
        return items
    }
}