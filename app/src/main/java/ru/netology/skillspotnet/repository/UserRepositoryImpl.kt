package ru.netology.skillspotnet.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.skillspotnet.api.ApiService
import ru.netology.skillspotnet.dao.UserDao
import ru.netology.skillspotnet.dto.User
import ru.netology.skillspotnet.entity.toDto
import ru.netology.skillspotnet.entity.toUserEntity
import ru.netology.skillspotnet.error.ApiError
import ru.netology.skillspotnet.error.NetworkError
import ru.netology.skillspotnet.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService,
) : UserRepository {

    override val data: Flow<List<User>> =
        userDao.getAllUsers()
            .map { it.toDto() }
            .flowOn(Dispatchers.Default)

    override suspend fun getAllUsers() {
        try {
           userDao.getAllUsers()
            val response = apiService.getUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            userDao.insert(body.toUserEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}