package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiLoginError
import ru.netology.nmedia.model.StateModel
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userApiService: ApiService,
) : ViewModel() {
    val data = MutableLiveData<Token>()
    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    fun authenticationUser(login: String, password: String) {
        viewModelScope.launch {
            _dataState.postValue(StateModel(loading = true))
            try {
                val response = userApiService.updateUser(login, password)
                if (!response.isSuccessful) {
                    throw ApiLoginError(response.message())
                }
                _dataState.postValue(StateModel())
                val body = response.body() ?: throw ApiLoginError(response.message())
                data.value = Token(body.id, body.token)
            } catch (e: IOException) {
                _dataState.postValue(StateModel(error = true))
            } catch (e: Exception) {
                _dataState.postValue(StateModel(signInError = true))
            }
        }
    }
}