package ru.netology.skillspotnet.dto

import android.net.Uri
import ru.netology.skillspotnet.enumeration.AttachmentType

data class Media(val url: String)

data class MediaUpload(val file: Uri, val type: AttachmentType? = null)