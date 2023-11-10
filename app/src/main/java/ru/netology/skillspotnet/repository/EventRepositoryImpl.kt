package ru.netology.skillspotnet.repository

import android.content.ContentResolver
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.skillspotnet.api.ApiService
import ru.netology.skillspotnet.dao.EventDao
import ru.netology.skillspotnet.dao.EventRemoteKeyDao
import ru.netology.skillspotnet.db.AppDb
import ru.netology.skillspotnet.dto.Attachment
import ru.netology.skillspotnet.dto.Event
import ru.netology.skillspotnet.dto.Media
import ru.netology.skillspotnet.dto.MediaUpload
import ru.netology.skillspotnet.entity.EventEntity
import ru.netology.skillspotnet.entity.toEntity
import ru.netology.skillspotnet.enumeration.AttachmentType
import ru.netology.skillspotnet.error.ApiError
import ru.netology.skillspotnet.error.AppError
import ru.netology.skillspotnet.error.NetworkError
import ru.netology.skillspotnet.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EventRepositoryImpl @Inject constructor(
    appDb: AppDb,
    private val eventDao: EventDao,
    eventRemoteKeyDao: EventRemoteKeyDao,
    private val apiService: ApiService,
    private val contentResolver: ContentResolver,
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 5),
        remoteMediator = EventRemoteMediator(apiService, appDb, eventDao, eventRemoteKeyDao),
        pagingSourceFactory = eventDao::getPagingSource,
    ).flow.map { pagingData ->
        pagingData.map(EventEntity::toDto)
    }

    override suspend fun getAllEvents() {
        try {
            val response = apiService.getAllEvents()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insertEvents(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(120_000L)
            val response = apiService.getNewerEvents(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insertEvents(body.toEntity())
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun saveEvent(
        event: Event,
        upload: MediaUpload?,
        attachmentType: AttachmentType?
    ) {
        try {
            val eventWithAttachment = upload?.let {
                uploadEvent(it)
            }?.let {
                when (attachmentType) {
                    AttachmentType.AUDIO -> event.copy(
                        attachment = Attachment(
                            it.url,
                            AttachmentType.AUDIO
                        )
                    )

                    AttachmentType.VIDEO -> event.copy(
                        attachment = Attachment(
                            it.url,
                            AttachmentType.VIDEO
                        )
                    )

                    AttachmentType.IMAGE -> event.copy(
                        attachment = Attachment(
                            it.url,
                            AttachmentType.IMAGE
                        )
                    )

                    else -> event
                }
            }
            val response = apiService.saveEvent(eventWithAttachment ?: event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeEventById(id: Long) {
        try {
            eventDao.removeEventById(id)
            val response = apiService.removeEventById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeEventById(id: Long) {
        try {
            eventDao.likeEventById(id)
            val response = apiService.likeEventById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun dislikeEventById(id: Long) {
        try {
            eventDao.unlikeEventById(id)
            val response = apiService.dislikeEventById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun participateEventById(id: Long) {
        try {
            eventDao.participate(id)
            val response = apiService.participate(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun unParticipateEventById(id: Long) {
        try {
            eventDao.unParticipate(id)
            val response = apiService.unParticipate(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun uploadEvent(upload: MediaUpload): Media {
        try {
            val media = contentResolver.openInputStream(upload.file)?.use {
                MultipartBody.Part.createFormData(
                    "file", "file", it.readBytes().toRequestBody()
                )
            }
            requireNotNull(media) {
                "Resource ${upload.file} not found"
            }

            val response = apiService.uploadMedia(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}


