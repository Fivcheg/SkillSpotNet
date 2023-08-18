package ru.netology.nmedia.repository

import dagger.Binds
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Job

interface JobRepository {
    val data: Flow<List<Job>>
    suspend fun saveJob(job: Job)
    suspend fun getByUserIdJob(id: Long)
    suspend fun removeByIdJob(id: Long)
}