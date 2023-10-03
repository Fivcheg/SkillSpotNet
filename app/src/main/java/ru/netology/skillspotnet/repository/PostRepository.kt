package ru.netology.skillspotnet.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.skillspotnet.dto.Media
import ru.netology.skillspotnet.dto.MediaUpload
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.enumeration.AttachmentType

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun save(post: Post, upload: MediaUpload?, attachmentType: AttachmentType?)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun upload(upload: MediaUpload): Media
}
