package ru.netology.skillspotnet.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.skillspotnet.dto.Event
import ru.netology.skillspotnet.dto.Media
import ru.netology.skillspotnet.dto.MediaUpload
import ru.netology.skillspotnet.enumeration.AttachmentType

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun getAllEvents()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun saveEvent(event: Event, upload: MediaUpload?, attachmentType: AttachmentType?)
    suspend fun removeEventById(id: Long)
    suspend fun likeEventById(id: Long)
    suspend fun dislikeEventById(id: Long)
    suspend fun uploadEvent(upload: MediaUpload): Media
    suspend fun participateEventById(id: Long)
    suspend fun unParticipateEventById(id: Long)

}
