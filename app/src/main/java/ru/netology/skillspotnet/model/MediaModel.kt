package ru.netology.skillspotnet.model

import android.net.Uri
import ru.netology.skillspotnet.enumeration.AttachmentType

data class MediaModel(
    val uri: Uri? = null,
    val type: AttachmentType? = null,
)