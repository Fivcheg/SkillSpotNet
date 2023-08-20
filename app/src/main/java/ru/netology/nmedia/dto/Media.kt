package ru.netology.nmedia.dto

import android.net.Uri
import ru.netology.nmedia.enumeration.AttachmentType

data class Media(val url: String)

data class MediaUpload(val file: Uri, val type: AttachmentType? = null)