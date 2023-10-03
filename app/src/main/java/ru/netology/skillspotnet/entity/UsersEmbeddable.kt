package ru.netology.skillspotnet.entity

import ru.netology.skillspotnet.dto.Users

data class UsersEmbeddable(
    var name: String,
    var avatar: String?,
) {
    fun toDto() = Users(name, avatar)

    companion object {
        fun fromDto(dto: Users?) = dto?.let {
            UsersEmbeddable(it.name, it.avatar)
        }
    }
}


