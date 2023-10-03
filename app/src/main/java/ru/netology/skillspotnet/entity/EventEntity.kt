package ru.netology.skillspotnet.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.skillspotnet.dto.Event
import ru.netology.skillspotnet.enumeration.EventType


@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    var authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    @Embedded("coords_")
    val coords: CoordinatesEmbeddable?,
    val typeEvent: EventType,
    val likeOwnerIds: List<Int> = emptyList(),
    val likedByMe: Boolean,
    //   val speakerIds: List<Int>?,
    //   val participantsIds: List<Int>?,
    val participatedByMe: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    val link: String?,
    val ownedByMe: Boolean,
    @Embedded
    val users: UsersEmbeddable?,
) {
    fun toDto(): Event {
        val event = Event(
            id = id,
            authorId = authorId,
            author = author,
            authorAvatar = authorAvatar,
            authorJob = authorJob,
            content = content,
            datetime = datetime,
            published = published,
            coords = coords?.toDto(),
            type = typeEvent,
            likeOwnerIds = likeOwnerIds,
            likedByMe = likedByMe,
            //      speakerIds = speakerIds,
            //     participantsIds = participantsIds,
            participatedByMe = participatedByMe,
            attachment = attachment?.toDto(),
            link = link,
            ownedByMe = ownedByMe,
            users = users?.toDto()
        )
        return event
    }

    companion object {
        fun fromDto(dto: Event): EventEntity {
            return EventEntity(
                id = dto.id,
                authorId = dto.authorId,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                authorJob = dto.authorJob,
                content = dto.content,
                datetime = dto.datetime,
                published = dto.published,
                coords = CoordinatesEmbeddable.fromDto(dto.coords),
                typeEvent = dto.type,
                likeOwnerIds = dto.likeOwnerIds,
                likedByMe = dto.likedByMe,
                //        speakerIds = dto.speakerIds,
                //        participantsIds = dto.participantsIds,
                participatedByMe = dto.participatedByMe,
                attachment = AttachmentEmbeddable.fromDto(dto.attachment),
                link = dto.link,
                ownedByMe = dto.ownedByMe,
                users = UsersEmbeddable.fromDto(dto.users)
            )
        }
    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)
