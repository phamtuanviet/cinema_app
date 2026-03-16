package com.example.myapplication.utils
import java.time.LocalDateTime
import java.time.ZoneId

fun formatSeconds(seconds: Long): String {

    val minutes = seconds / 60
    val remainSeconds = seconds % 60

    return "%02d:%02d".format(minutes, remainSeconds)
}



fun parseExpireTime(expiresAt: String?): Long? {

    if (expiresAt == null) return null

    return try {

        LocalDateTime
            .parse(expiresAt)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    } catch (e: Exception) {
        null
    }
}