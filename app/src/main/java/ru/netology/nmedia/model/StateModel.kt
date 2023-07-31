package ru.netology.nmedia.model

data class StateModel(
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val error: Boolean = false,
    val signInError: Boolean = false,
)