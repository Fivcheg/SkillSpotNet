package ru.netology.nmedia.model

import android.net.Uri
import ru.netology.nmedia.enumeration.AttachmentType

data class MediaModel(
    val uri: Uri? = null,
    val type: AttachmentType? = null,
)