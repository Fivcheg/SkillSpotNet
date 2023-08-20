package ru.netology.nmedia.dto

import java.io.File
import java.io.InputStream

data class Media(val url: String)

data class MediaUpload(val file: File)