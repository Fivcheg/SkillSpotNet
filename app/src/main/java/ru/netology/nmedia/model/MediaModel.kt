package ru.netology.nmedia.model

import android.net.Uri
import ru.netology.nmedia.enumeration.AttachmentType
import java.io.File
import java.io.InputStream

//data class MediaModel(
//    val uri: Uri? = null,
//    val inputStream: InputStream? = null,
//    val type: AttachmentType? = null,
//)

data class MediaModel(
    val uri: Uri? = null,
    val inputFile: File? = null,
    val type: AttachmentType? = null,
)