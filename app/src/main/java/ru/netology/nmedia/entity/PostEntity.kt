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
    val content: String,
    val published: String,
    var authorAvatar: String?,
    val likedByMe: Boolean,
    val likes: Int = 0,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    val ownedByMe: Boolean

    ) {
    fun toDto(): Post {
        val post = Post(
            id,
            authorId,
            author,
            authorAvatar,
            content,
            published,
            likedByMe,
            likes,
            attachment?.toDto(),
            ownedByMe
        )
        return post
    }

    companion object {
        fun fromDto(dto: Post): PostEntity {
            return PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.content,
                dto.published,
                dto.authorAvatar,
                dto.likedByMe,
                dto.likes,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.ownedByMe
            )
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
