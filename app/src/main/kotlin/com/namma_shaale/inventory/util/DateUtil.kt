package com.namma_shaale.inventory.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtil {
    fun formatDate(timestamp: Long): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        )
        return DateTimeFormatter.ofPattern("dd MMM yyyy").format(dateTime)
    }

    fun formatDateTime(timestamp: Long): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        )
        return DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm").format(dateTime)
    }

    fun getMonthStartTimestamp(monthsBack: Int = 0): Long {
        val now = LocalDateTime.now()
        val startOfMonth = now.minusMonths(monthsBack.toLong())
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
        return startOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
