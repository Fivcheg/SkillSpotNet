package ru.netology.skillspotnet.repository

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.skillspotnet.api.ApiService
import ru.netology.skillspotnet.dao.JobDao
import ru.netology.skillspotnet.dto.Job
import ru.netology.skillspotnet.entity.JobEntity
import ru.netology.skillspotnet.entity.toDto
import ru.netology.skillspotnet.entity.toEntity
import ru.netology.skillspotnet.error.ApiError
import ru.netology.skillspotnet.error.NetworkError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: ApiService,
) : JobRepository {

    override val data: Flow<List<Job>> = jobDao.getJob()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    private val _data = MutableLiveData<List<Job>>()

    override suspend fun saveJob(job: Job) {
        try {
            val response = apiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insertJob(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw ru.netology.skillspotnet.error.UnknownError
        }
    }

    override suspend fun getByUserIdJob(id: Long) {
        try {
            jobDao.removeAll()
            val response = apiService.getJobByUserId(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            _data.postValue(body)
            jobDao.insertJobs(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw ru.netology.skillspotnet.error.UnknownError
        }
    }

    override suspend fun removeByIdJob(id: Long) {
        try {
            jobDao.removeJobById(id)
            val response = apiService.removeJobById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}