package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Event

data class EventModel(
    val posts: List<Event> = emptyList(),
)
