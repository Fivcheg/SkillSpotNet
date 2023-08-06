package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.enumeration.EventType

sealed class FeedItem {
    abstract val id: Long
}

data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
) : FeedItem()

data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val likeOwnerIds: List<Int>? = null,
    val mentionIds: List<Int>? = null,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
//    val likes: Int = 0,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val users: Users? = null,
) : FeedItem()

data class Job(
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
)

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

data class Attachment(
    val url: String,
    val type: AttachmentType,
)

data class Coordinates(
    val lat: String,
    val long: String,
)

data class Users(
    val name: String,
    val avatar: String? = null,
)

