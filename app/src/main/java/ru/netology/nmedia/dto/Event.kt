package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.EventType

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates? = null,
    val type: EventType,
    val likedByMe: Boolean,
    val participatedByMe: Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val users: Users,
)