package ru.netology.skillspotnet.entity

import ru.netology.skillspotnet.dto.Coordinates

data class CoordinatesEmbeddable(
    var latitude: String = "",
    var longitude: String = "",
) {
    fun toDto() = Coordinates(latitude, longitude)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            CoordinatesEmbeddable(it.lat, it.long)
        }
    }
}


