package ru.netology.skillspotnet.model

import ru.netology.skillspotnet.dto.Event

data class EventModel(
    val posts: List<Event> = emptyList(),
)
