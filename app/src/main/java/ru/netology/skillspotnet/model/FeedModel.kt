package ru.netology.skillspotnet.model

import ru.netology.skillspotnet.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
)
