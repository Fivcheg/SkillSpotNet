package ru.netology.skillspotnet.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.skillspotnet.dto.Post

interface WallRepository {
    //val data: Flow<PagingData<Post>>
    fun loadWall(userId: Long): Flow<PagingData<Post>>
}