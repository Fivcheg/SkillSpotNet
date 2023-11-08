package ru.netology.skillspotnet.dto

import ru.netology.skillspotnet.enumeration.AttachmentType

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
    val likeOwnerIds: List<Int> = emptyList(),
    val mentionIds: List<Int> = emptyList(),
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val users: Users? = null,
) : FeedItem()

data class Attachment(
    val url: String,
    val type: AttachmentType,
)

data class Coordinates(
    val lat: String,
    val long: String,
)

data class Users(
    val name: String = " ",
    val avatar: String? = null,
)

