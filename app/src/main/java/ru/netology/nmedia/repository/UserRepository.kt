package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.User


interface UserRepository {

    val data: Flow<List<User>>

    suspend fun getAllUsers()
}