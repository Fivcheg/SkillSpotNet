package ru.netology.skillspotnet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.netology.skillspotnet.auth.AppAuth
import ru.netology.skillspotnet.dto.Ad
import ru.netology.skillspotnet.dto.Post
import ru.netology.skillspotnet.repository.WallRepository
import javax.inject.Inject
import kotlin.random.Random


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WallViewModel @Inject constructor(
    private val repository: WallRepository,
    val auth: AppAuth,
) : ViewModel() {


    private fun getWall(id: Long) = repository.loadWall(id)
        .map { pagingData ->
            pagingData.insertSeparators(
                generator = { before, after ->
                    if (before?.id?.rem(5) != 0L) null else
                        Ad(
                            //TODO !!!!!!!!
                            Random.nextLong(),
                            "@sample/ads_images/",
                            "figma.jpg"
                        )
                }
            )
        }
        .cachedIn(viewModelScope)

    fun loadWall(id: Long) = auth.authStateFlow
        .flatMapLatest { (userId, _) ->
            getWall(id)
                .map { pagingData ->
                    pagingData.map { item ->
                        if ((item !is Post)) item else item.copy(
                            ownedByMe = item.authorId == userId,
                            likedByMe = item.likeOwnerIds.contains(userId.toInt())
                        )
                    }
                }
        }
}

