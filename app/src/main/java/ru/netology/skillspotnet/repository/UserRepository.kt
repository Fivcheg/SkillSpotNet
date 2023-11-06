package ru.netology.skillspotnet.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.skillspotnet.dto.User

interface UserRepository {
    val data: Flow<List<User>>
    suspend fun getAllUsers()
}