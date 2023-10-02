package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface WallRepository {
    val data: Flow<PagingData<Post>>
    fun loadWall(userId: Long): Flow<PagingData<Post>>
}