package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.enumeration.AttachmentType

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun getAllEvents()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun saveEvent(event: Event, upload: MediaUpload?, attachmentType: AttachmentType?)
    suspend fun removeEventById(id: Long)
    suspend fun likeEventById(id: Long)
    suspend fun dislikeEventById(id: Long)
    suspend fun uploadEvent(upload: MediaUpload): Media
}
