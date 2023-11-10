package ru.netology.skillspotnet.dto

import ru.netology.skillspotnet.enumeration.EventType

sealed class EventItem {
    abstract val id: Long
}

data class AdEvent(
    override val id: Long,
    val url: String,
    val image: String,
) : EventItem() {
    companion object {
        val testEvent1 = AdEvent(
            id = 1,
            url = "https://www.netology.ru",
            image = "https://cs13.pikabu.ru/post_img/2023/05/20/9/1684592976157428245.jpg",
        )
        val testEvent2 = AdEvent(
            id = 2,
            url = "https://www.google.ru",
            image = "https://mobile-cuisine.com/wp-content/uploads/2013/02/pintrest-marketing.jpg",
        )
    }
}

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


