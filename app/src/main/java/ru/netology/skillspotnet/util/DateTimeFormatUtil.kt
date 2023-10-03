package ru.netology.skillspotnet.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


fun realTimeFormat(datetime: String): String {
    val transformation = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.ROOT)
        .withZone(ZoneId.systemDefault())
    return transformation.format(Instant.parse(datetime))
}


