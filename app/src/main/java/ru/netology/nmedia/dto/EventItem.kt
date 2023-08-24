package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.EventType


sealed class EventItem {
    abstract val id: Long
}

data class AdEvent(
    override val id: Long,
    val url: String,
    val image: String,
) : EventItem()

data class Event(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates? = null,
    val type: EventType,
    val likeOwnerIds: List<Int>? = null,
    val likedByMe: Boolean,
    val speakerIds: List<Int>? = null,
    val participantsIds: List<Int>? = null,
    val participatedByMe: Boolean,
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    val users: Users? = null,
) : EventItem()