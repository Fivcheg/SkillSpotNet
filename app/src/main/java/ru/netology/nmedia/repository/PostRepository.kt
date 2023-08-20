package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    fun getNewerCount(id: Long): Flow<Int>
//    suspend fun save(post: Post)
//    suspend fun saveWithAttachment(post: Post, upload: MediaUpload, type: AttachmentType)
    suspend fun save(post: Post, upload: MediaUpload?, attachmentType: AttachmentType?)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun upload(upload: MediaUpload): Media
}
