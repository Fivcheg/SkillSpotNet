package ru.netology.nmedia.viewmodel

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
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.AdEvent
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.EventItem
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.enumeration.EventType
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.MediaModel
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.EventRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    datetime = "",
    published = "",
    coords = null,
    type = EventType.OFFLINE,
    likeOwnerIds = null,
    likedByMe = false,
    speakerIds = null,
    participantsIds = null,
    participatedByMe = false,
    attachment = null,
    link = null,
    ownedByMe = false,
    users = null,
)

private val noPhoto = PhotoModel()
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
                            //TODO !!!!!!!!
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

    private val edited = MutableLiveData(empty)
    private val _eventCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _eventCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

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

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAllEvents()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    repository.saveEvent(
                        it, _media.value?.uri?.let { MediaUpload(it) }, _media.value?.type
                    )
                    _eventCreated.value = Unit
                    edited.value = empty
                    _photo.value = noPhoto
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

    fun changePhoto(uri: Uri?, inputFile: File?) {
        _photo.value = PhotoModel(uri, inputFile)
    }

    fun changeMedia(
        uri: Uri?,
        type: AttachmentType?,
    ) {
        _media.value = MediaModel(uri, type)
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeEventById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeEventById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun dislikeById(id: Long) = viewModelScope.launch {
        try {
            repository.dislikeEventById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
}