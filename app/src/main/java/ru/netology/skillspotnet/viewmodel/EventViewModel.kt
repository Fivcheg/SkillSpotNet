package ru.netology.skillspotnet.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.skillspotnet.auth.AppAuth
import ru.netology.skillspotnet.dto.AdEvent
import ru.netology.skillspotnet.dto.Coordinates
import ru.netology.skillspotnet.dto.Event
import ru.netology.skillspotnet.dto.EventItem
import ru.netology.skillspotnet.dto.MediaUpload
import ru.netology.skillspotnet.dto.Users
import ru.netology.skillspotnet.enumeration.AttachmentType
import ru.netology.skillspotnet.enumeration.EventType
import ru.netology.skillspotnet.model.FeedModelState
import ru.netology.skillspotnet.model.MediaModel
import ru.netology.skillspotnet.repository.EventRepository
import ru.netology.skillspotnet.util.SingleLiveEvent
import javax.inject.Inject
import kotlin.random.Random

val emptyUser = Users(
    name = "",
    avatar = null
)
private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    datetime = "2023-01-27T17:00:00.000Z",
    published = "2023-01-27T17:00:00.000Z",
    coords = null,
    type = EventType.OFFLINE,
    likeOwnerIds = emptyList(),
    likedByMe = false,
    speakerIds = emptyList(),
    participantsIds = emptyList(),
    participatedByMe = false,
    attachment = null,
    link = null,
    ownedByMe = false,
    users = emptyUser,
)

private val noMedia = MediaModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    auth: AppAuth,
) : ViewModel() {
    private val cached: Flow<PagingData<EventItem>> = repository
        .data
        .map { pagingData ->
            pagingData.insertSeparators(
                generator = { before, after ->
                    if (before?.id?.rem(5) != 0L) null else
                        AdEvent(
                            Random.nextLong(),
                            "@sample/ads_images/",
                            "figma.jpg"
                        )
                }
            )
        }
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<EventItem>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached
                .map { pagingData ->
                    pagingData.map { item ->
                        if (item !is Event) item else item.copy(ownedByMe = item.authorId == myId)
                    }
                }
        }

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val edited = MutableLiveData(empty)
    private val _eventCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _eventCreated

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    init {
        loadEvents()
    }

    private fun loadEvents() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun saveEvent() {
        edited.value?.let {
            viewModelScope.launch {
                _dataState.postValue(FeedModelState(loading = true))
                try {
                    repository.saveEvent(
                        it, _media.value?.uri?.let { MediaUpload(it) }, _media.value?.type
                    )
                    _dataState.value = FeedModelState()
                    _eventCreated.value = Unit
                    edited.value = empty
                    _media.value = noMedia
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun edit(event: Event) {
        edited.value = event
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun changeDateTime(date: String) {
        val dateTime = date.trim()
        if (edited.value?.datetime == dateTime) {
            return
        }
        edited.value = edited.value?.copy(datetime = dateTime)
    }

    fun changeCoords(lat: String, long: String) {
        val coordinates = Coordinates(lat, long)
        if (edited.value?.coords == coordinates) {
            return
        }
        edited.value = edited.value?.copy(coords = coordinates)
    }

    fun changeMedia(
        uri: Uri?,
        type: AttachmentType?,
    ) {
        _media.value = MediaModel(uri, type)
    }

    fun removeEventById(id: Long) = viewModelScope.launch {
        try {
            repository.removeEventById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun pickSpeakerIds(id: Long) {
        edited.value?.let {
            edited.value = edited.value?.copy(
                speakerIds = it.speakerIds.plus(id.toInt())
            )
        }
    }

    fun unPickSpeakerIds(id: Long) {
        edited.value?.let {
            edited.value = edited.value?.copy(
                speakerIds = it.speakerIds.minus(id.toInt())
            )
        }
    }

    fun likeEventById(id: Long) = viewModelScope.launch {
        try {
            repository.likeEventById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun dislikeEventById(id: Long) = viewModelScope.launch {
        try {
            repository.dislikeEventById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
    fun participateEventById(id: Long) = viewModelScope.launch {
        try {
            repository.participateEventById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun unParticipateEventById(id: Long) = viewModelScope.launch {
        try {
            repository.unParticipateEventById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
}