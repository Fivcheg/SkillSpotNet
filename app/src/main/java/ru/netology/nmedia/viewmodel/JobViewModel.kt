package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.JobModel
import ru.netology.nmedia.repository.JobRepository
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Job(
    id = 0,
    name = "",
    position = "",
    start = "",
    finish = null,
    link = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: JobRepository,
    auth: AppAuth,
) : ViewModel() {

    val data: Flow<List<Job>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data.map {
                JobModel()
                it.map {
                    it.copy(ownedByMe = userId.value == myId)
                }
            }
        }
    private val userId = MutableLiveData<Long>()
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated

    fun setId(id: Long) {
        userId.value = id
    }

    internal fun loadJobs(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getByUserIdJob(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    _dataState.value = FeedModelState(loading = true)
                    repository.saveJob(it)
                    _jobCreated.value = Unit
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        edited.value = empty
    }

    fun edit(job: Job) {
        edited.value = job
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeByIdJob(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun editJob(name: String, position: String, start: String, finish: String?, link: String?) {
        val nameJob = name.trim()

        if (edited.value?.name != nameJob) {
            edited.value = edited.value?.copy(name = nameJob)
        }

        val positionJob = position.trim()
        if (edited.value?.position != positionJob) {
            edited.value = edited.value?.copy(position = positionJob)
        }

        val startJob = start.trim()
        if (edited.value?.position != startJob) {
            edited.value = edited.value?.copy(start = startJob)
        }

        val finishJob = finish?.trim()
        if (edited.value?.position != finishJob) {
            edited.value = edited.value?.copy(finish = finishJob)
        }

        val linkJob = link?.trim()
        if (edited.value?.position != linkJob) {
            edited.value = edited.value?.copy(link = linkJob)
        }
    }

    fun startDate(date: String) {
        edited.value = edited.value?.copy(start = date)
    }

    fun finishDate(date: String) {
        edited.value = edited.value?.copy(finish = date)
    }
}
