package ru.netology.skillspotnet.dto

import ru.netology.skillspotnet.enumeration.EventType

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
    val likeOwnerIds: List<Int> = emptyList(),
    val likedByMe: Boolean,
    val speakerIds: List<Int> = emptyList(),
    val participantsIds: List<Int> = emptyList(),
    val participatedByMe: Boolean,
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    val users: Users,
) : EventItem() {
    companion object {
        val emptyUser = Users(
            name = "",
            avatar = null
        )
        val emptyEvent = Event(
            id = 0,
            authorId = 0,
            author = "",
            authorAvatar = null,
            authorJob = null,
            content = "",
            datetime = "",
            published = "",
            coords = null,
            type = EventType.OFFLINE,
            likeOwnerIds = emptyList(),
            likedByMe = false,
            speakerIds = emptyList(),
            participantsIds = emptyList(),
            participatedByMe = false,
            attachment = null,
            link = null,
            ownedByMe = false,
            users = emptyUser,
        )
    }

}