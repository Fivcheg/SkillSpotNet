package ru.netology.skillspotnet.entity

import ru.netology.skillspotnet.dto.Coordinates

data class CoordinatesEmbeddable(
    var lat: String = "",
    var long: String = "",
) {
    fun toDto() = Coordinates(lat, long)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            CoordinatesEmbeddable(it.lat, it.long)
        }
    }
}


