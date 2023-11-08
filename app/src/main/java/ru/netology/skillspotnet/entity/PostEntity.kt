package ru.netology.skillspotnet.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.skillspotnet.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    var authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    @Embedded
    val coords: CoordinatesEmbeddable?,
    val link: String? = null,
    val likeOwnerIds: List<Int> = emptyList(),
    val mentionIds: List<Int> = emptyList(),
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    val ownedByMe: Boolean,
    @Embedded
    val users: UsersEmbeddable?,
) {
    fun toDto(): Post {
        return Post(
            id = id,
            authorId = authorId,
            author = author,
            authorAvatar = authorAvatar,
            authorJob = authorJob,
            content = content,
            published = published,
            coords = coords?.toDto(),
            link = link,
            likeOwnerIds = likeOwnerIds,
            mentionIds = mentionIds,
            mentionedMe = mentionedMe,
            likedByMe = likedByMe,
            attachment = attachment?.toDto(),
            ownedByMe = ownedByMe,
            users = users?.toDto()
        )
    }

    companion object {
        fun fromDto(dto: Post): PostEntity {
            return PostEntity(
                id = dto.id,
                authorId = dto.authorId,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                authorJob = dto.authorJob,
                content = dto.content,
                published = dto.published,
                coords = CoordinatesEmbeddable.fromDto(dto.coords),
                link = dto.link,
                likeOwnerIds = dto.likeOwnerIds,
                mentionIds = dto.mentionIds,
                mentionedMe = dto.mentionedMe,
                likedByMe = dto.likedByMe,
                attachment = AttachmentEmbeddable.fromDto(dto.attachment),
                ownedByMe = dto.ownedByMe,
                users = UsersEmbeddable.fromDto(dto.users)
            )
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
