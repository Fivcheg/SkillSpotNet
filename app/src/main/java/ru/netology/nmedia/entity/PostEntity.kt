package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    var authorAvatar: String?,
    val content: String,
    val published: String,
    @Embedded
    val coords: CoordinatesEmbeddable?,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    val ownedByMe: Boolean,
    @Embedded
    val users: UsersEmbeddable?,
) {
    fun toDto(): Post {
        val post = Post(
            id,
            authorId,
            author,
            authorAvatar,
            content,
            published,
            coords?.toDto(),
            mentionedMe,
            likedByMe,
            attachment?.toDto(),
            ownedByMe,
            users?.toDto()
        )
        return post
    }

    companion object {
        fun fromDto(dto: Post): PostEntity {
            return PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                CoordinatesEmbeddable.fromDto(dto.coords),
                dto.mentionedMe,
                dto.likedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.ownedByMe,
                UsersEmbeddable.fromDto(dto.users)
            )
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
