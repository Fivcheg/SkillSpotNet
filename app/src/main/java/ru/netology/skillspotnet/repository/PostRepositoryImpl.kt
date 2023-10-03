package ru.netology.skillspotnet.repository

import android.content.ContentResolver
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.skillspotnet.api.ApiService
import ru.netology.skillspotnet.dao.PostDao
import ru.netology.skillspotnet.dao.PostRemoteKeyDao
import ru.netology.skillspotnet.db.AppDb
import ru.netology.skillspotnet.dto.Attachment
import ru.netology.skillspotnet.dto.Media
import ru.netology.skillspotnet.dto.MediaUpload
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.entity.PostEntity
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
class PostRepositoryImpl @Inject constructor(
    appDb: AppDb,
    private val postDao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val apiService: ApiService,
    private val contentResolver: ContentResolver,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5),
        remoteMediator = PostRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(120_000L)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun save(post: Post, upload: MediaUpload?, attachmentType: AttachmentType?) {
        try {
            val postWithAttachment = upload?.let {
                upload(it)
            }?.let {
                when (attachmentType) {
                    AttachmentType.AUDIO -> post.copy(
                        attachment = Attachment(
                            it.url,
                            AttachmentType.AUDIO
                        )
                    )

                    AttachmentType.VIDEO -> post.copy(
                        attachment = Attachment(
                            it.url,
                            AttachmentType.VIDEO
                        )
                    )

                    AttachmentType.IMAGE -> post.copy(
                        attachment = Attachment(
                            it.url,
                            AttachmentType.IMAGE
                        )
                    )
                    else ->  post
                }

            }
            val response = apiService.save(postWithAttachment ?: post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }


            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun removeById(id: Long) {
        try {
            postDao.removeById(id)
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            postDao.likeById(id)
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            postDao.unlikeById(id)
            val response = apiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
                val media = contentResolver.openInputStream(upload.file)?.use {
                MultipartBody.Part.createFormData(
                    "file", "file", it.readBytes().toRequestBody()
                )
            }

            requireNotNull(media) {
                "Resource ${upload.file} not found"
            }

            val response = apiService.upload(media)
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


