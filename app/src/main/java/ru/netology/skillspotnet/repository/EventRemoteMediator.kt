package ru.netology.skillspotnet.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.skillspotnet.api.ApiService
import ru.netology.skillspotnet.dao.EventDao
import ru.netology.skillspotnet.dao.EventRemoteKeyDao
import ru.netology.skillspotnet.db.AppDb
import ru.netology.skillspotnet.entity.EventEntity
import ru.netology.skillspotnet.entity.EventRemoteKeyEntity
import ru.netology.skillspotnet.entity.toEntity
import ru.netology.skillspotnet.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val service: ApiService,
    private val db: AppDb,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
) : RemoteMediator<Int, EventEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> service.getEventLatest(state.config.initialLoadSize)
                LoadType.PREPEND -> {
                    val id = eventRemoteKeyDao.max() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getEventAfter(id, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getEventBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )
            if (body.isEmpty()) {
                return MediatorResult.Success(endOfPaginationReached = false)
            }
            db.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        eventRemoteKeyDao.removeAll()
                        eventRemoteKeyDao.insertEvent(
                            listOf(
                                EventRemoteKeyEntity(
                                    type = EventRemoteKeyEntity.KeyType.AFTER,
                                    key = body.first().id,
                                ),
                                EventRemoteKeyEntity(
                                    type = EventRemoteKeyEntity.KeyType.BEFORE,
                                    key = body.last().id,
                                ),
                            )
                        )
                        eventDao.removeAll()
                    }

                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insertEvent(
                            EventRemoteKeyEntity(
                                type = EventRemoteKeyEntity.KeyType.AFTER,
                                key = body.first().id,
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insertEvent(
                            EventRemoteKeyEntity(
                                type = EventRemoteKeyEntity.KeyType.BEFORE,
                                key = body.last().id,
                            )
                        )
                    }
                }
                eventDao.insertEvents(body.toEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}