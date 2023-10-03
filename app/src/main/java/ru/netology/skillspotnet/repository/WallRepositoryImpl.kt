package ru.netology.skillspotnet.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.skillspotnet.api.ApiService
import ru.netology.skillspotnet.dao.PostDao
import ru.netology.skillspotnet.dao.PostRemoteKeyDao
import ru.netology.skillspotnet.db.AppDb
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.entity.PostEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : WallRepository {


//    @OptIn(ExperimentalPagingApi::class)
//    override val data: Flow<PagingData<Post>> = Pager(
//        config = PagingConfig(pageSize = 5),
//        remoteMediator = PostRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao),
//        pagingSourceFactory = postDao::pagingSource,
//    ).flow.map { pagingData ->
//        pagingData.map(PostEntity::toDto)
//    }

    @OptIn(ExperimentalPagingApi::class)
    override fun loadWall(userId: Long): Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(
            service = apiService,
            postDao = postDao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb,
            authorId = userId,
        ),
        pagingSourceFactory = { postDao.getPagingSource(userId) }
    ).flow
        .map {
            it.map(PostEntity::toDto)
        }
}

