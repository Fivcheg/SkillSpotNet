package ru.netology.skillspotnet.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.skillspotnet.dto.Job

interface JobRepository {
    val data: Flow<List<Job>>
    suspend fun saveJob(job: Job)
    suspend fun getByUserIdJob(id: Long)
    suspend fun removeByIdJob(id: Long)
}