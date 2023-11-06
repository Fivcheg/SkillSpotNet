package ru.netology.skillspotnet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.skillspotnet.api.ApiService
import ru.netology.skillspotnet.dto.User
import ru.netology.skillspotnet.model.FeedModelState
import ru.netology.skillspotnet.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val apiService: ApiService,
) : ViewModel() {

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userIds = MutableLiveData<List<Int>>()
    val userIds: LiveData<List<Int>>
        get() = _userIds

    val data: LiveData<List<User>> =
        repository.data.asLiveData(Dispatchers.Default)

    init {
        loadUsers()
    }

    private fun loadUsers() = viewModelScope.launch {
        try {
            repository.getAllUsers()
            _dataState.value = FeedModelState(loading = true)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAllUsers()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun getUserById(id: Long) = viewModelScope.launch {
        _dataState.postValue(FeedModelState(loading = true))
        try {
            val response = apiService.getUserById(id)
            if (response.isSuccessful) {
                _user.value = response.body()
            }
            _dataState.postValue(FeedModelState())
        } catch (e: Exception) {
            _dataState.postValue(FeedModelState(error = true))
        }
    }

    fun getUsersIds(viewUsers: List<Int>) =
        viewModelScope.launch {
            _userIds.value = viewUsers
        }

}