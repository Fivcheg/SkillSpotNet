package ru.netology.nmedia.entity


data class LikeOwnerIdsConverter(
    var likeOwnerIds: List<Int>
) {
    fun toDto() = likeOwnerIds

    companion object {
        fun fromDto(dto: List<Int>?) {
            dto?.let{
                LikeOwnerIdsConverter(it)}
        }
    }
}



